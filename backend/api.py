"""This file imports the API configuration
    for routing"""

from flask import Blueprint
from flask_restful import Api

from .resources.user import UserLogin, UserRegister, UserLogout
from .resources.restaurant import Restaurants

API_BP = Blueprint("api", __name__, url_prefix="/api")
API = Api(API_BP)

API.add_resource(UserLogin, "/users/login/")
API.add_resource(UserLogout, "/users/logout/")
API.add_resource(UserRegister, "/users/register/")
API.add_resource(Restaurants, "/restaurant/all")
