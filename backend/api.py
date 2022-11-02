"""This file imports the API configuration
    for routing"""

from flask import Blueprint
from flask_restful import Api

from .resources.user import *
from .resources.restaurant import Restaurants, Order

API_BP = Blueprint("api", __name__, url_prefix="/api")
API = Api(API_BP)

API.add_resource(GetUsers, "/users/")
API.add_resource(UserLogin, "/users/login/")
API.add_resource(UserLogout, "/users/logout/")
API.add_resource(UserRegister, "/users/register/")
API.add_resource(UserAdminElevate, "/users/admin/elevate/")
API.add_resource(UserAdminDelevate, "/users/admin/delevate/")
API.add_resource(Restaurants, "/restaurant/all/")
API.add_resource(Order, "/restaurant/order/")