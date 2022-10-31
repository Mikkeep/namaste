""" Methods for Users """

import json
from flask import request, Response, session
from flask_restful import Resource

from backend.models import User
from ..constants import JSON
from ..utils import fetch_item, get_db
from sqlite3 import IntegrityError
from jsonschema import validate, ValidationError, draft7_format_checker


class UserLogin(Resource):
    """Methods for single user"""

    def post(self):
        """Post method functionality for single user"""
        if not request.json:
            return Response(
                status=415,
                response=json.dumps("Request content type must be JSON"),
            )

        try:
            validate(
                request.json,
                User.json_schema(),
                format_checker=draft7_format_checker,
            )
        except ValidationError:
            return Response(status=400, response=json.dumps("Invalid JSON"))

        username = request.json.get("username")
        password = request.json.get("password")

        if not username:
            return Response(status=401, response=json.dumps("No username provided!"))
        if not password:
            return Response(status=401, response=json.dumps("No password provided!"))

        command = "SELECT * FROM user WHERE username = '%s' AND password = '%s'" % (
            username,
            password,
        )
        user = fetch_item(command)

        if user is None:
            return Response(
                status=404,
                response=json.dumps("User does not exist!"),
            )

        data = {
            "name": user["username"],
        }

        session["id"] = user["id"]

        return Response(
            status=200,
            response=json.dumps(data, indent=4, separators=(",", ": ")),
            mimetype=JSON,
        )


class UserRegister(Resource):
    """Methods for creating an user"""

    def post(self):
        """Post method for creating an user"""

        if not request.json:
            return Response(
                status=415,
                response=json.dumps("Request content type must be JSON"),
            )

        try:
            validate(
                request.json,
                User.json_schema(),
                format_checker=draft7_format_checker,
            )
        except ValidationError:
            return Response(status=400, response=json.dumps("Invalid JSON"))

        username = request.json.get("username")
        password = request.json.get("password")

        db = get_db()

        if not username:
            return Response(status=401, response=json.dumps("No username provided!"))
        if not password:
            return Response(status=401, response=json.dumps("No password provided!"))

        try:
            db.execute(
                "INSERT INTO user (username, password, is_admin) VALUES (?, ?, ?)",
                (username, password, False),
            )
            db.commit()
            db.close()
        except IntegrityError as e:
            return Response(status=401, response=json.dumps("User already exists."))

        return Response(status=200, response=json.dumps("User created successfully."))


class UserLogout(Resource):
    """Method for logging user out"""

    def post(self):
        """Post method functionality for logging user out"""
        session["id"] = None
        return Response(
            status=200, response=json.dumps("User logged out successfully.")
        )


class UserAdmin(Resource):
    """Check if the user is admin then elevate the specified user to admin"""

    def post(self):
        if not request.json:
            return Response(
                status=415,
                response=json.dumps("Request content type must be JSON"),
            )

        try:
            validate(
                request.json,
                User.json_schema(),
                format_checker=draft7_format_checker,
            )
        except ValidationError:
            return Response(status=400, response=json.dumps("Invalid JSON"))

        admin_name = session["id"]

        command = f"SELECT * FROM users WHERE username = '{admin_name}"
        admin = fetch_item(command)

        if admin.is_admin is False:
            return Response(status=403, response=json.dumps("Unauthorized admin elevation."))

        username = request.json.get("username")

        db = get_db()

        if not username:
            return Response(status=401, response=json.dumps("No username provided!"))

        command = "SELECT * FROM user WHERE username = '%s'" % (
            username,
        )
        user = fetch_item(command)

        if user is None:
            return Response(
                status=404,
                response=json.dumps("User does not exist!"),
            )

        try:
            db.execute(
                "UPDATE user SET is_admin = '?' WHERE username = '?'", (True, username)
            )
            db.commit()
            db.close
        except IntegrityError as e:
            return Response(status=400, response=json.dumps("Something went wrong."))
        