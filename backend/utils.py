"""
Utility methods for Flask
"""

import json
import sqlite3
from flask import g, session, Response
from jsonschema import draft7_format_checker, validate, ValidationError
from .constants import DB_LOCATION
from .models import User
from . import DB


def get_db():
    if "db" not in g:
        g.db = sqlite3.connect(
            DB_LOCATION,
            detect_types=sqlite3.PARSE_DECLTYPES,
        )
        g.db.row_factory = dict_factory

    return g.db


def close_db(e=None):
    db = g.pop("db", None)

    if db is not None:
        db.close()


def dict_factory(cursor, row):
    col_names = [col[0] for col in cursor.description]
    return {key: value for key, value in zip(col_names, row)}


def fetch_item(command):
    item = get_db().execute(command).fetchone()
    close_db()
    return item


def fetch_items(command):
    items = get_db().execute(command).fetchall()
    close_db()
    return items


def ensure_login(func):
    """Checks session["id"] state before resource access"""

    def server(*args, **kwargs):
        if session.get("id") == None:
            return Response(
                status=401,
                response=json.dumps("Please log in."),
            )
        exec = func(*args, **kwargs)
        return exec

    return server


def ensure_admin(func):
    """Checks session["id"] state before resource access"""

    def server(*args, **kwargs):
        if session.get("id") == None:
            return Response(
                status=401,
                response=json.dumps("Please log in."),
            )
        user_priv = DB.session.query(User).filter(User.id == session["id"]).first()
        if not user_priv.is_admin:
            return Response(
                status=403,
                response=json.dumps("This is an admin API call!"),
            )
        exec = func(*args, **kwargs)
        return exec

    return server


def check_request_json(request, DB_model):
    """Check if the request json is of proper schema"""
    if not request.json:
        return Response(
            status=415,
            response=json.dumps("Request content type must be JSON"),
        )
    try:
        validate(
            request.json,
            DB_model.json_schema(),
            format_checker=draft7_format_checker,
        )
    except ValidationError:
        return Response(status=400, response=json.dumps("Invalid JSON"))
