""" Methods for Restaurants """

import json
from flask import Response, session, request
from flask_restful import Resource
from .. import DB

from backend.models import Restaurant, Item, Orders
from ..constants import JSON, HIDDEN_RESTAURANT
import random
from ..utils import (
    ensure_login,
    get_db,
    check_request_json,
)


class Restaurants(Resource):
    """Methods for restaurants listed in the database"""

    @ensure_login
    def get(self):
        """Return list of all restaurants"""
        if session["id"] == 1:
            restaurants = DB.session.query(Restaurant).all()
        else:
            restaurants = (
                DB.session.query(Restaurant)
                .filter(Restaurant.name != HIDDEN_RESTAURANT)
                .all()
            )

        listing = []
        products = {}
        for restaurant in restaurants:
            items = (
                DB.session.query(Item).filter(Item.restaurant_id == restaurant.id).all()
            )
            for item in items:
                products = {"name": item.name, "id": item.id}

            info = {
                "id": restaurant.id,
                "name": restaurant.name,
                "description": restaurant.description,
                "products": products,
            }
            listing.append(info)

        data = {
            "restaurants": listing,
        }

        return Response(
            status=200,
            response=json.dumps(data, separators=(",", ": ")),
            mimetype=JSON,
        )

    def post(self):
        return Response(status=200)


class Order(Resource):
    """Make an order to a restaurant"""

    @ensure_login
    def post(self):
        """Make an order
        Order needs to be in json format with the following values
        user_id: id of the user making the order, gotten with session
        rest_id: id of the restaurant being ordered from
        item_id: id(s) of the item being ordered in a list
        amount: amount of food being ordered in a list
        description: the location of the order"""

        if check_request_json(request, Orders):
            return check_request_json(request, Orders)

        try:
            user_id = session[
                "id"
            ]  # Should this be possible to be done without logging in, to allow uses order for other users?
            rest_id = request.json.get("rest_id")
            item_id = request.json.get("item_id")
            amount = request.json.get("amount")
            desc = request.json.get("description")
        except:
            return Response(status=400, response=json.dumps("Invalid JSON"))
        print(user_id, rest_id, item_id, amount, desc)
        db = get_db()
        db.execute(
            "INSERT INTO orders (user_id, rest_id, item_id, amount, description) VALUES (?, ?, ?, ?, ?)",
            (user_id, rest_id, item_id, amount, desc),
        )
        db.commit()
        db.close()

        return Response(status=200, response=json.dumps("Order complete!"))


class OrderHistory(Resource):
    @ensure_login
    def post(self):
        """Get all the orders made by session ID"""
        if request.json.get("user_id"):
            user_id = request.json.get("user_id")
        else:
            user_id = session["id"]

        orders = DB.session.query(Orders).filter(Orders.user_id == user_id).all()

        resp = {}
        
        cardnum = ""
        for _ in range(0, 12):
            val = random.randint(1, 9)
            cardnum = cardnum + str(val)

        for i, order in enumerate(orders):
            resp[i] = {}
            query_result = DB.session.query(Item, Restaurant).filter(
                Item.id == order.item_id, Restaurant.id == order.rest_id
            )
            for item, restaurant in query_result:
                resp[i]["item_name"] = item.name
                resp[i]["rest_name"] = restaurant.name
                resp[i]["amount"] = order.amount
                resp[i]["description"] = order.description
                resp[i]["cardnumber"] = cardnum

        if resp == None:
            return Response(status=200, response=json.dumps("Orders not found!"))
        else:
            return Response(
                status=200, response=json.dumps(resp, separators=(",", ": "))
            )
