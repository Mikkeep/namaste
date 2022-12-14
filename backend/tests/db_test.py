import json
import os
import mock
import sys
import pytest
import tempfile
from jsonschema import validate, draft7_format_checker
from click.testing import CliRunner
from flask import session

from sqlalchemy import event
from sqlalchemy.engine import Engine
from sqlalchemy.exc import IntegrityError


SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
sys.path.append(os.path.dirname(SCRIPT_DIR))
from backend import DB, create_app
from backend.models import User, Orders, Item, Restaurant
from backend.utils import get_db, dict_factory, ensure_login, check_request_json
from backend.resources.user import *
from backend.resources.restaurant import *
from backend.resources.file_handling import GetFile


@event.listens_for(Engine, "connect")
def set_sqlite_pragma(dbapi_connection, connection_record):
    """Set db engine"""
    cursor = dbapi_connection.cursor()
    cursor.execute("PRAGMA foreign_keys=ON")
    cursor.close()


@pytest.fixture
def client():
    """Setting the client for testing purpose"""
    db_fd, db_fname = tempfile.mkstemp()
    config = {"SQLALCHEMY_DATABASE_URI": "sqlite:///" + db_fname, "TESTING": True}

    app = create_app()

    app = create_app(config)

    with app.app_context():
        DB.create_all()
        _fill_db()

        yield app.test_client()

    os.close(db_fd)
    os.unlink(db_fname)


def _create_user(username="", password="", is_admin=False):
    """
    Creates User model
    """
    return User(username=username, password=password, is_admin=is_admin)


def _create_item(name=""):
    """
    Creates Item model
    """
    return Item(name=name)


def _create_restaurant(name="", description="", icon=""):
    """Create Restaurant model"""
    return Restaurant(name=name, description=description, icon=icon)


def _fill_db():
    """
    Tests populating database
    """
    test_user = _create_user("Bob", "bobword", True)
    test_item = _create_item(name="burger")
    test_restaurant = _create_restaurant(
        "Bob's burgers", "place for burgers", icon="bobi"
    )
    # test_order = _create_order(1, 2, 3, "nice", 2)

    item = Item(
        name="burgeri",
        restaurant_id="1",
    )
    DB.session.add(item)
    DB.session.add(test_user)
    DB.session.add(test_item)
    DB.session.add(test_restaurant)
    # DB.session.add(test_order)
    DB.session.commit()


def test_start(client):
    """Test that api responds on start"""
    resp = client.get("/api/")
    assert resp.status_code == 200


def _valid_user_json(username="bob", password="bob", is_admin=True):
    """
    Returns User json to check schema
    """
    return {"username": f"{username}", "password": f"{password}", "is_admin": is_admin}


def _valid_restaurant_json(
    name="BobsPizza", description="fastfood such as pizza", icon="bobi"
):
    """
    Returns User json to check schema
    """
    return {"name": f"{name}", "description": f"{description}", "icon": f"{icon}"}


def _valid_order_json(
    user_id=1, rest_id=2, item_id=3, description="From BobsPizza", amount=2
):
    """
    Returns Order json to check schema
    """
    return {
        "user_id": f"{user_id}",
        "rest_id": f"{rest_id}",
        "item_id": f"{item_id}",
        "description": f"{description}",
        "amount": f"{amount}",
    }


def _valid_item_json(name="Pizza Balooza"):
    """
    Returns Item json to check schema
    """
    return {"name": f"{name}"}


def test_schema_validation():
    """Test validation for database schemas"""
    validate(
        _valid_user_json("Bob", "bobword", True),
        User.json_schema(),
        format_checker=draft7_format_checker,
    )
    validate(
        _valid_restaurant_json("BobsPizza", "fastfood such as pizza"),
        Restaurant.json_schema(),
        format_checker=draft7_format_checker,
    )
    validate(
        _valid_order_json("BobsPizzaOrder", "From BobsPizza", amount=2),
        Orders.json_schema(),
        format_checker=draft7_format_checker,
    )
    validate(
        _valid_item_json("PizzaBaloosa"),
        Item.json_schema(),
        format_checker=draft7_format_checker,
    )


class TestUser(object):
    """Test for User"""

    def testUsersUnauth(self, client):
        """Test get method on Users"""
        resp = client.get("/api/users/")
        print(resp.data)
        assert resp.status_code == 401

    def testNonAdminUserAdminPromote(self, client):
        """Test admin elevate on non admin account"""
        data = {"username": "Bob", "password": "password"}
        resp = client.post(
            "/api/users/login/",
            json=data,
            headers={"Content-type": "application/json", "Accept": "application/json"},
        )
        resp = client.post("/api/users/admin/elevate/")
        assert resp.status_code == 403

    def testAdminUserAdminPromote(self, client):
        """Test admin elevate on admin account"""
        data = {"username": "Admin", "password": "supersecurepassword123456"}
        resp = client.post(
            "/api/users/login/",
            json=data,
            headers={"Content-type": "application/json", "Accept": "application/json"},
        )
        userdata = {"command": "Bob"}
        resp = client.post(
            "/api/users/admin/elevate/",
            json=userdata,
            headers={"Content-type": "application/json", "Accept": "application/json"},
        )
        assert resp.status_code == 200

    def testFaultyUserAdminPromote(self, client):
        """Test admin elevate on admin account"""
        data = {"username": "Admin", "password": "supersecurepassword123456"}
        resp = client.post(
            "/api/users/login/",
            json=data,
            headers={"Content-type": "application/json", "Accept": "application/json"},
        )
        userdata = {"wrong_field": "Bob"}
        resp = client.post(
            "/api/users/admin/elevate/",
            json=userdata,
            headers={"Content-type": "application/json", "Accept": "application/json"},
        )
        assert resp.status_code == 401

    def testAdminUserAdminDemote(self, client):
        """Test admin elevate on admin account"""
        data = {"username": "Admin", "password": "supersecurepassword123456"}
        resp = client.post(
            "/api/users/login/",
            json=data,
            headers={"Content-type": "application/json", "Accept": "application/json"},
        )
        userdata = {"command": "Bob"}
        resp = client.post(
            "/api/users/admin/elevate/",
            json=userdata,
            headers={"Content-type": "application/json", "Accept": "application/json"},
        )
        userdata = {"command": "Bob"}
        resp = client.post(
            "/api/users/admin/delevate/",
            json=userdata,
            headers={"Content-type": "application/json", "Accept": "application/json"},
        )
        assert resp.status_code == 200

    def testFaultyUserAdminDemote(self, client):
        """Test admin elevate on admin account"""
        data = {"username": "Admin", "password": "supersecurepassword123456"}
        resp = client.post(
            "/api/users/login/",
            json=data,
            headers={"Content-type": "application/json", "Accept": "application/json"},
        )
        userdata = {"command": "Bob"}
        resp = client.post(
            "/api/users/admin/elevate/",
            json=userdata,
            headers={"Content-type": "application/json", "Accept": "application/json"},
        )
        userdata = {"wrong_field": "Bob"}
        resp = client.post(
            "/api/users/admin/delevate/",
            json=userdata,
            headers={"Content-type": "application/json", "Accept": "application/json"},
        )
        assert resp.status_code == 401

    def test_access_session(self, client):
        data = {"username": "Bob", "password": "password"}
        resp = client.post(
            "/api/users/login/",
            json=data,
            headers={"Content-type": "application/json", "Accept": "application/json"},
        )

        assert resp.status_code == 200

    def test_faulty_login(self, client):
        data = {"username": "Bob", "pass": "password"}
        resp = client.post(
            "/api/users/login/",
            json=data,
            headers={"Content-type": "application/json", "Accept": "application/json"},
        )

        assert resp.status_code == 400

    def test_faulty_register(self, client):
        data = {"username": "Bob", "pass": "password"}
        resp = client.post(
            "/api/users/register/",
            json=data,
            headers={"Content-type": "application/json", "Accept": "application/json"},
        )

        assert resp.status_code == 400

    def test_access_register(self, client):
        data = {"username": "testibob1", "password": "bobword"}
        resp = client.post(
            "/api/users/register/",
            json=data,
            headers={"Content-type": "application/json", "Accept": "application/json"},
        )
        assert resp.status_code == 200

    def test_get_restaurantUnauth(self, client):
        """Test get method"""
        resp = client.get("/api/restaurant/all/")
        assert resp.status_code == 401


def test_user_already_registered(client):
    with client as session:
        bob = client.post(
            "/api/users/register/", json={"username": "Bob", "password": "password"}
        )
        assert bob.status_code == 401


def test_login_fail_no_account(client):
    with client as session:
        bob = client.post(
            "/api/users/login/", json={"username": "Esa", "password": "password"}
        )
        assert bob.status_code == 401


def test_access_logout(client):
    with client as session:
        bob = client.post(
            "/api/users/login/", json={"username": "Bob", "password": "password"}
        )
        resp = UserLogout.post(client)
        assert resp.status_code == 200
        assert bob.status_code == 200


def test_get_users_as_admin(client):
    with client as session:
        bob = client.post(
            "/api/users/login/",
            json={"username": "Admin", "password": "supersecurepassword123456"},
        )
        assert bob.status_code == 200
        resp = GetUsers.get(client)
        assert resp.status_code == 200


def test_register_password_pass(client):
    resp = check_register_password("aasi")
    assert resp.status_code == 200


def test_register_password_fail_spec(client):
    resp = check_register_password("aasi_")
    assert resp.status_code == 400


def test_register_password_pass_long(client):
    resp = check_register_password("aasi123456")
    assert resp.status_code == 400


def test_user_admin_modify(client):
    resp = user_admin_modify(True, "Admin")
    assert resp.status_code == 200


def test_user_admin_modify_fail(client):
    resp = user_admin_modify(True, "Admin")
    assert resp.status_code == 200


def test_restaurants(client):
    with client as session:
        bob = client.post(
            "/api/users/login/",
            json={"username": "Admin", "password": "supersecurepassword123456"},
        )
        resp = Restaurants.get(client)
        resp2 = Restaurants.post(client)
        assert resp.status_code == 200
        assert resp2.status_code == 200
        assert bob.status_code == 200


def test_restaurantsNonAdmin(client):
    with client as session:
        bob = client.post(
            "/api/users/login/",
            json={"username": "Bob", "password": "password"},
        )
        resp = Restaurants.get(client)
        resp2 = Restaurants.post(client)
        assert resp.status_code == 200
        assert resp2.status_code == 200
        assert bob.status_code == 200


def test_restaurantsNonAdminOrder(client):
    with client as session:
        bob = client.post(
            "/api/users/login/",
            json={"username": "Bob", "password": "password"},
        )
        resp = client.post(
            "/api/restaurant/order/",
            json={
                "rest_id": "1",
                "item_id": "1",
                "amount": 1,
                "description": "some desc",
            },
        )
        assert resp.status_code == 200


def test_restaurantsNonAdminAll(client):
    with client as session:
        bob = client.post(
            "/api/users/login/",
            json={"username": "Bob", "password": "password"},
        )
        resp = client.get(
            "/api/restaurant/all/",
        )
        assert resp.status_code == 200


def test_orders(client):
    with client as session:
        bob = client.post(
            "api/users/login/", json={"username": "Bob", "password": "password"}
        )
        resp2 = Order.post(client)
        assert bob.status_code == 200
        assert resp2.status_code == 400


def test_file_handling(client):
    with client as session:
        bob = client.post(
            "api/users/login/", json={"username": "Bob", "password": "password"}
        )
        resp = GetFile.get(client)
        assert bob.status_code == 200
        assert resp.status_code == 200


def test_order_history(client):
    # create a new OrderHistory object
    with client as session:
        client.post(
            "api/users/login/", json={"username": "Bob", "password": "password"}
        )
        order_history = OrderHistory()

        # call the post method of the OrderHistory object
        response = order_history.post()

        # assert that the status code of the response is 200
        assert response.status_code == 200
        assert response.data == b"{}"


def test_order_history_in_json(client):
    # create a new OrderHistory object
    with client as session:
        client.post(
            "api/users/login/", json={"username": "Bob", "password": "password"}
        )
        resp = client.post(
            "api/restaurant/order/history/",
            json={"username": "Bob", "password": "password", "user_id": "1"},
        )

        # call the post method of the OrderHistory object
        # assert that the status code of the response is 200
        assert resp.status_code == 200


def test_check_request_json(request):
    """Test that check_request works"""
    request = mock.Mock()
    request.json = None

    # Create a mock DB_model object
    DB_model = mock.Mock()

    # Call the check_request_json function with the mock request and DB_model objects
    response = check_request_json(request, DB_model)
    assert response.status_code == 415
