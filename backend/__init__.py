"""
Init file for starting the application
"""

import os
from flask import Flask, Response
from flask_sqlalchemy import SQLAlchemy
from backend.constants import *

DB = SQLAlchemy()

# Based on http://flask.pocoo.org/docs/1.0/tutorial/factory/#the-application-factory
# Modified to use Flask SQLAlchemy
def create_app(test_config=None):
    """
    Starts the app
    """

    app = Flask(__name__, instance_relative_config=True)
    app.config.from_mapping(
        SECRET_KEY="dev",
        SQLALCHEMY_DATABASE_URI="sqlite:///" + os.path.join(app.instance_path, DB_NAME),
        SQLALCHEMY_TRACK_MODIFICATIONS=False,
    )

    if test_config is None:
        app.config.from_pyfile("config.py", silent=True)
    else:
        app.config.from_mapping(test_config)

    try:
        os.makedirs(app.instance_path)
    except OSError:
        pass

    from . import models

    DB.init_app(app)

    from . import api

    app.cli.add_command(models.init_db_command)
    app.cli.add_command(models.generate_test_data)
    app.register_blueprint(api.API_BP)

    @app.route("/api/")
    def view():
        """Return api entrypoint."""
        return Response(
            status=200,
        )

    return app
