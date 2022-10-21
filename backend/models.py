"""
Contains database models and click methods
"""

import click
from flask.cli import with_appcontext
from sqlalchemy import Column, Integer, String, ForeignKey, Boolean
from sqlalchemy.orm import relationship, backref
from . import DB


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
        return schema


class Restaurant(DB.Model):
    """
    Restaurant database model
    """

    __tablename__ = "restaurant"
    id = Column(Integer, primary_key=True)
    name = Column(String(100), unique=True, nullable=False)
    description = Column(String(200), unique=False, nullable=False)

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
    restaurant_id = Column(Integer, ForeignKey("restaurant.id"))
    description = Column(String(2000), nullable=False)

    user = relationship("User", backref=backref("user", cascade="all, delete-orphan"))
    restaurant = relationship(
        "Restaurant", backref=backref("restaurant", cascade="all, delete-orphan")
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
    name = Column(String(100), unique=True, nullable=False)

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
    print("Generating test users...")
    p_0 = User(username="Admin", password="supersecurepassword123456", is_admin=True)
    DB.session.add(p_0)
    DB.session.commit()
    p_1 = User(username="Bob", password="password", is_admin=False)
    DB.session.add(p_1)
    DB.session.commit()
    print("Test generation of Users successful")
    print("Generating restaurants...")
    r_0 = Restaurant(
        name="Bob's burgers", description="Almost as good as Alices aspargus'."
    )
    r_1 = Restaurant(
        name="Alice's aspargus", description="Definitely better than Bob's burgers."
    )
    r_2 = Restaurant(
        name="VIP Lounge", description="A secret VIP lounge for high class customers."
    )
    DB.session.add(r_0)
    DB.session.add(r_1)
    DB.session.add(r_2)
    DB.session.commit()
    print("Restaurants generation successful.")
    print()
    print("Test generation successful!")
