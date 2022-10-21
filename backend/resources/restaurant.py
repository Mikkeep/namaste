""" Methods for Restaurants """

import json
from flask import Response, session
from flask_restful import Resource
from .. import DB

from backend.models import Restaurant
from ..constants import JSON, HIDDEN_RESTAURANT
from ..utils import ensure_login


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
            info = {
                "id": restaurant.id,
                "name": restaurant.name,
                "description": restaurant.description,
            }
            listing.append(info)

        data = {
            "restaurants": listing,
        }

        return Response(
            status=200,
            response=json.dumps(data, indent=4, separators=(",", ": ")),
            mimetype=JSON,
        )

    def post(self):
        pass
