""" Methods for Restaurants """

import json
from flask import request, Response
from flask_restful import Resource

from backend.models import User, Restaurant
from ..constants import JSON
from ..utils import fetch_items
from sqlite3 import IntegrityError
from jsonschema import validate, ValidationError, draft7_format_checker


class Restaurant(Resource):
    """Methods for restaurants listed in the database"""

    def get(self):
        command = "SELECT * FROM restaurant WHERE NOT name = 'Bob burgers'"
        restaurants = fetch_items(command)

        if restaurants is None:
            return Response(
                status=404,
                response=json.dumps("Restaurants do not exist!"),
            )

        data = {
            "restaurants": restaurants,
        }

        return Response(
            status=200,
            response=json.dumps(data, indent=4, separators=(",", ": ")),
            mimetype=JSON,
        )

    def post(self):
        pass
