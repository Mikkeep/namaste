""" Methods for Users """

import json
from string import ascii_letters, digits
from flask import request, Response, session
from flask_restful import Resource

from backend.models import User
from ..constants import JSON
from ..utils import (
    ensure_login,
    ensure_admin,
    fetch_item,
    fetch_items,
    get_db,
    check_request_json,
)
from sqlite3 import IntegrityError


class GetUsers(Resource):
    """Get a json of all users"""

    @ensure_admin
    def get(self):
        command = "SELECT * FROM user"
        users = fetch_items(command)

        resp = {}
        for i, user in enumerate(users):
            resp[i] = {}
            for key in user:
                resp[i][key] = user[key]

        if users == None:
            return Response(status=200, response=json.dumps("Users not found!"))
        else:
            return Response(
                status=200, response=json.dumps(resp, indent=4, separators=(",", ": "))
            )


class UserLogin(Resource):
    """Methods for single user"""

    def post(self):
        """Post method functionality for single user"""

        if check_request_json(request, User):
            return check_request_json(request, User)

        username = request.json.get("username")
        password = request.json.get("password")

        command = "SELECT * FROM user WHERE username = '%s' AND password = '%s'" % (
            username,
            password,
        )
        user = fetch_item(command)

        if user is None:
            return Response(
                status=401,
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

        if check_request_json(request, User):
            return check_request_json(request)

        username = request.json.get("username")
        password = request.json.get("password")

        db = get_db()

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

    @ensure_login
    def post(self):
        """Post method functionality for logging user out"""
        session["id"] = None
        return Response(
            status=200, response=json.dumps("User logged out successfully.")
        )


class UserAdminElevate(Resource):
    """Check if the user is admin then elevate the specified user to admin"""

    @ensure_admin
    def post(self):
        """POST method functionality for admin elevation"""

        if check_request_json(request, User):
            return check_request_json(request)

        username = request.json.get("command")

        if not username:
            return Response(
                status=401, response=json.dumps("No command field provided!")
            )

        success = user_admin_modify(True, username)
        if success:
            return user_admin_modify


class UserAdminDelevate(Resource):
    """Check if the user is admin then demote the specified user from admin"""

    @ensure_admin
    def post(self):
        """POST method for de-elevating user"""

        if check_request_json(request, User):
            return check_request_json(request)

        username = request.json.get("command")

        if not username:
            return Response(
                status=401, response=json.dumps("No command field provided!")
            )

        success = user_admin_modify(False, username)
        if success:
            return user_admin_modify


def user_admin_modify(statement, username):
    """Modify is_admin property in the database
    statement: boolean True or False
    username: name of the user to modify"""
    try:
        db = get_db()
        db.execute(
            f"UPDATE user SET is_admin = '{statement}' WHERE username = '{username}'"
        )
        db.commit()
        db.close
    except IntegrityError as e:
        return Response(status=400, response=json.dumps("Something went wrong."))


def check_register_password(password: str):
    if len(password) > 3 and len(password) < 9:
        for letter in password:
            if letter not in ascii_letters or letter not in digits:
                return Response(
                    status=400,
                    response=json.dumps(
                        "Password can only contain letters and numbers!"
                    ),
                )
    else:
        return Response(
            status=400, response=json.dumps("Password length is between 4-8!")
        )
