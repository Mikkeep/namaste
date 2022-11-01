"""
Contains database models and click methods
"""

import click
from flask.cli import with_appcontext
from sqlalchemy import Column, Integer, String, ForeignKey, Boolean
import sqlalchemy
from sqlalchemy.orm import relationship, backref
from . import DB
from .constants import restaurants, users, items


class User(DB.Model):
    """
    User database model
    """

    __tablename__ = "user"
    id = Column(Integer, primary_key=True)
    username = Column(String(100), unique=True, nullable=False)
    password = Column(String(100), nullable=False)
    is_admin = Column(Boolean, nullable=False)

    @staticmethod
    def json_schema():
        """Returns the schema for User"""
        schema = {"type": "object", "required": ["username", "password"]}
        props = schema["properties"] = {}
        props["username"] = {"description": "username", "type": "string"}
        props["password"] = {"description": "password", "type": "string"}
        props["command"] = {"description": "command", "type": "string"}
        return schema


class Restaurant(DB.Model):
    """
    Restaurant database model
    """

    __tablename__ = "restaurant"
    id = Column(Integer, primary_key=True)
    name = Column(String(100), unique=True, nullable=False)
    description = Column(String(200), unique=False, nullable=False)
    icon = Column(String(200), unique=False, nullable=False)

    @staticmethod
    def json_schema():
        """Returns the schema for Restaurant"""
        schema = {"type": "object", "required": ["name", "description"]}
        props = schema["properties"] = {}
        props["name"] = {"description": "name", "type": "string"}
        props["description"] = {"description": "description", "type": "string"}
        return schema


class Orderitem(DB.Model):
    """
    Order items database model
    """

    __tablename__ = "orderitem"
    id = Column(Integer, ForeignKey("order.id"), primary_key=True)
    ingredient_id = Column(Integer, ForeignKey("item.id"), primary_key=True)
    amount = Column(Integer)

    order_rel = relationship(
        "Order",
        backref=backref("orderitems", cascade="all, delete-orphan"),
    )
    item = relationship(
        "Item",
        backref=backref("orderitems", cascade="all, delete-orphan"),
    )

    @staticmethod
    def json_schema():
        """
        Define the JSON schema for database model
        """
        schema = {"type": "object", "required": ["name", "amount"]}
        props = schema["properties"] = {}
        props["name"] = {"description": "Ingredients ID", "type": "string"}
        props["amount"] = {
            "description": "Amount of item",
            "type": "number",
        }
        return schema


class Order(DB.Model):
    """
    Order database model
    """

    __tablename__ = "order"
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey("user.id"))
    rest_id = Column(Integer, ForeignKey("restaurant.id"))
    description = Column(String(2000), nullable=False)

    user = relationship("User", backref=backref("user", cascade="all, delete-orphan"))
    restaurant = relationship(
        "Restaurant", backref=backref("restaurants_item", cascade="all, delete-orphan")
    )

    @staticmethod
    def json_schema():
        """
        Define the JSON schema for database model
        """
        schema = {"type": "object", "required": ["name", "description"]}
        props = schema["properties"] = {}
        props["description"] = {
            "description": "Description of the order",
            "type": "string",
        }
        return schema


class Item(DB.Model):
    """
    Item database model
    """

    __tablename__ = "item"
    id = Column(Integer, primary_key=True)
    name = Column(String(100), unique=False, nullable=False)
    restaurant_id = Column(Integer, ForeignKey("restaurant.id"))
    restaurant = relationship(
        "Restaurant", backref=backref("restaurant", cascade="all, delete-orphan")
    )

    @staticmethod
    def json_schema():
        """Returns the schema for Item"""
        schema = {"type": "object", "required": ["name"]}
        props = schema["properties"] = {}
        props["name"] = {"description": "Name of item", "type": "string"}
        return schema


@click.command("init-db")
@with_appcontext
def init_db_command():
    """
    Makes 'flask init-db' possible from command line. Initializes DB by
    creating the tables.
    Example from:
    https://github.com/enkwolf/pwp-course-sensorhub-api-example/blob/master/sensorhub/models.py
    """
    DB.create_all()
    print("DB init successful")


@click.command("testgen")
@with_appcontext
def generate_test_data():
    """
    Generate content for database for testing
    """
    try:
        # users and restaurants are imported from constants
        print()
        print("Generating test users...")
        for entry, value in users.items():
            usr = User(
                username=value.get("name"),
                password=value.get("password"),
                is_admin=value.get("is_admin"),
            )
            DB.session.add(usr)
        DB.session.commit()

        print("Test generation of Users successful")
        print()
        print("Generating restaurants...")

        for entry, value in restaurants.items():
            rest = Restaurant(
                name=value.get("name"),
                description=value.get("description"),
                icon=value.get("icon"),
            )
            DB.session.add(rest)
        DB.session.commit()
        print("Restaurants generation successful.")
        print()
        print("Test generation successful!")
        print()
        print("Generating items for restaurants...")
        for entry, value in items.items():
            item = Item(
                name=value.get("name"),
                restaurant_id=value.get("res_id"),
            )
            DB.session.add(item)
        DB.session.commit()

        print("Test generation of items successful")
        print()
    except sqlalchemy.exc.IntegrityError as e:
        print("\nDatabase is already populated with testgen data!\n")
        print("To use this command, delete existing database records and try again.\n")
        print("Database testgen aborted!")
