import json
import os
import sys
import pytest
import tempfile
from jsonschema import validate, draft7_format_checker
from click.testing import CliRunner

from sqlalchemy import event
from sqlalchemy.engine import Engine
from sqlalchemy.exc import IntegrityError


SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
sys.path.append(os.path.dirname(SCRIPT_DIR))
from backend import DB, create_app
from backend.resources import restaurant, user
from  backend.models import User, Orderitem, Order, Item, Restaurant
from backend.utils import get_db, dict_factory, ensure_login



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

    yield app.test_client()

    os.close(db_fd)
    os.unlink(db_fname)

def test_start(db_handle):
    """Test that api responds on start"""
    resp = db_handle.get("/api/")
    assert resp.status_code == 200

def _valid_user_json(username="bob", password="bob"):
    """
    Returns User json to check schema
    """
    return {
        "username": f"{username}",
        "password": f"{password}",
    }

def _valid_restaurant_json(name="BobsPizza", description="fastfood such as pizza"):
    """
    Returns User json to check schema
    """
    return {
        "name": f"{name}",
        "description": f"{description}",
    }

def _valid_orderitem_json(name="Pizza Balooza", amount=2):
    """
    Returns User json to check schema
    """
    return {
        "name": f"{name}",
        "amount": amount,
    }

def _valid_order_json(name="BobsPizzaOrder", description="From BobsPizza"):
    """
    Returns Order json to check schema
    """
    return {
        "name": f"{name}",
        "description": f"{description}",
    }

def _valid_item_json(name="Pizza Balooza"):
    """
    Returns Item json to check schema
    """
    return {
        "name": f"{name}"
    }

def _invalid_user_json(username="Bobb", password="boobboboob"):
    """
    Returns invalid User json to check schema
    """
    return {"nameeee": f"{username}", "password": f"{password}"}

def test_schema_validation():
    """Test validation for database schemas"""
    validate(
        _valid_user_json("Bob", "bobword"),
        User.json_schema(),
        format_checker=draft7_format_checker,
    )
    validate(
        _valid_restaurant_json("BobsPizza", "fastfood such as pizza"),
        Restaurant.json_schema(),
        format_checker=draft7_format_checker,
    )
    validate(
        _valid_orderitem_json("BobsPizza", 2),
        Orderitem.json_schema(),
        format_checker=draft7_format_checker,
    )
    validate(
        _valid_order_json("BobsPizzaOrder", "From BobsPizza"),
        Order.json_schema(),
        format_checker=draft7_format_checker,
    )
    validate(
        _valid_item_json("PizzaBaloosa"),
        Item.json_schema(),
        format_checker=draft7_format_checker,
    )
