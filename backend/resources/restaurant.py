""" Methods for Restaurants """

import json
from flask import Response, session, request
from flask_restful import Resource
from .. import DB

from backend.models import Restaurant, Item
from ..constants import JSON, HIDDEN_RESTAURANT
from ..utils import ensure_login, get_db


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
        for restaurant in restaurants:
            items = (
                DB.session.query(Item).filter(Item.restaurant_id == restaurant.id).all()
            )
            products = []
            if items:
                products = [item.name for item in items]
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
        pass


class Order(Resource):
    """Make an order to a restaurant"""
    
    @ensure_login
    def post(self):
        """Make an order
        Order needs to be in json format with the following values
        user_id: id of the user making the order, gotten with session
        rest_id: id of the restaurant being ordered from
        item_id: id of the item being ordered
        description: I have no idea what this is, but I'm guessing the location of the order"""
        if not request.json:
            return Response(
                status=415,
                response=json.dumps("Request content type must be JSON"),
            )
        
        try:
            user_id = session["id"] # Should this be possible to be done without logging in, to allow uses order for other users?
            rest_id = request.json.get("rest_id")
            item_id = request.json.get("item_id")
            desc = request.json.get("description")
        except:
            return Response(status=400, response=json.dumps("Invalid JSON"))

        db = get_db()
        try:
            db.execute(
                "INSERT INTO order (user_id, rest_id, description) VALUES (?, ?, ?)",
                (user_id, rest_id, desc)
            )
            db.commit()
            db.execute(
                "INSER INTO "
            )