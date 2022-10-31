""" Methods for Users """

import json
from flask import request, Response, session
from flask_restful import Resource

from backend.models import User
from ..constants import JSON
from ..utils import fetch_item, fetch_items, get_db
from sqlite3 import IntegrityError
from jsonschema import validate, ValidationError, draft7_format_checker


class GetUsers(Resource):
    """Get a json of all users"""

    def get(self):
        command = "SELECT * FROM user"
        users = fetch_items(command)
        print(users)

        if users == None:
            return Response(status=404, response=json.dumps("Users not found!"))
        else:
            return Response(status=200, response=json.dumps(users))


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


class UserAdminElevate(Resource):
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

        #admin_id = session["id"]
        admin_username = request.json.get("username")

        command = f"SELECT * FROM user WHERE username = '{admin_username}'"
        admin = fetch_item(command)

        if admin["is_admin"] is False:
            return Response(status=403, response=json.dumps("Unauthorized admin elevation."))

        username = request.json.get("command")

        if not username:
            return Response(status=401, response=json.dumps("No username provided!"))

        try:
            db = get_db()
            db.execute(
                f"UPDATE user SET is_admin = 'True' WHERE username = '{username}'"
            )
            db.commit()
            db.close
        except IntegrityError as e:
            return Response(status=400, response=json.dumps("Something went wrong."))

     
class UserAdminDelevate(Resource):
    """Check if the user is admin then demote the specified user from admin"""

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

        #admin_id = session["id"]
        admin_username = request.json.get("username")

        command = f"SELECT * FROM user WHERE username = '{admin_username}'"
        admin = fetch_item(command)

        if admin["is_admin"] is False:
            return Response(status=403, response=json.dumps("Unauthorized admin elevation."))

        username = request.json.get("command")

        if not username:
            return Response(status=401, response=json.dumps("No username provided!"))

        try:
            db = get_db()
            db.execute(
                f"UPDATE user SET is_admin = 'False' WHERE username = '{username}'"
            )
            db.commit()
            db.close
        except IntegrityError as e:
            return Response(status=400, response=json.dumps("Something went wrong."))