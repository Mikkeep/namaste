import json
import os
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
from  backend.models import User, Orders, Item, Restaurant
from backend.utils import get_db, dict_factory, ensure_login, check_request_json



@event.listens_for(Engine, "connect")
def set_sqlite_pragma(dbapi_connection, connection_record):
    """Set db engine"""
    cursor = dbapi_connection.cursor()
    cursor.execute("PRAGMA foreign_keys=ON")
    cursor.close()

@pytest.fixture
def db_handle():
    """Setting the db_handle for testing purpose"""
    db_fd, db_fname = tempfile.mkstemp()
    app = create_app
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

def _create_order(user_id=1, rest_id="", item_id="", description="", amount=""):
    """Create Order model"""
    return Orders(
        user_id=user_id,
        rest_id=rest_id,
        item_id=item_id,
        description=description,
        amount=amount
        )

def _fill_db():
    """
    Tests populating database
    """

    test_user = _create_user("Bob", "bobword", True)
    test_item = _create_item(name="burger")
    test_restaurant = _create_restaurant("Bob's burgers", "place for burgers", icon="bobi")
    # test_order = _create_order(1, 2, 3, "nice", 2)

    DB.session.add(test_user)
    DB.session.add(test_item)
    DB.session.add(test_restaurant)
    # DB.session.add(test_order)
    DB.session.commit()

def test_start(db_handle):
    """Test that api responds on start"""
    resp = db_handle.get("/api/")
    assert resp.status_code == 200

def _valid_user_json(username="bob", password="bob", is_admin=True):
    """
    Returns User json to check schema
    """
    return {
        "username": f"{username}",
        "password": f"{password}",
        "is_admin": is_admin
    }

def _valid_restaurant_json(name="BobsPizza", description="fastfood such as pizza", icon="bobi"):
    """
    Returns User json to check schema
    """
    return {
        "name": f"{name}",
        "description": f"{description}",
        "icon": f"{icon}"
    }

def _valid_order_json(user_id=1, rest_id=2, item_id=3, description="From BobsPizza", amount=2):
    """
    Returns Order json to check schema
    """
    return {
        "user_id": f"{user_id}",
        "rest_id": f"{rest_id}",
        "item_id": f"{item_id}",
        "description": f"{description}",
        "amount": f"{amount}"
    }

def _valid_item_json(name="Pizza Balooza"):
    """
    Returns Item json to check schema
    """
    return {
        "name": f"{name}"
    }

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

    def testUsersUnauth(self, db_handle):
        """Test get method on Users"""
        resp = db_handle.get("/api/users/")
        print(resp.data)
        assert resp.status_code == 401

    def test_access_session(self, db_handle):
        data = {"username": "Bob", "password": "bobword"}
        resp = db_handle.post("/api/users/login/", json=data, headers={
    'Content-type':'application/json', 
    'Accept':'application/json'
    })

        assert resp.status_code == 200
    
    def test_access_register(self, db_handle):
        data = {"username": "testibob1", "password": "bobword"}
        resp = db_handle.post("/api/users/register/", json=data, headers={
    'Content-type':'application/json',
    'Accept':'application/json'
    })
        assert resp.status_code == 200


    def test_get_restaurantUnauth(self, db_handle):
        """Test get method"""
        resp = db_handle.get("/api/restaurant/all/")
        assert resp.status_code == 401
