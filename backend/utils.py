"""
Utility methods for Flask
"""

import json
import sqlite3
from flask import g, session, Response
from .constants import DB_LOCATION


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
