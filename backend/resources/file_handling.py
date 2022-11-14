""" Methods for Files """

from flask import send_file
from flask_restful import Resource
from ..constants import JSON


class getFile(Resource):
    
    def get(self):
        return send_file(".\\resources\\file.pdf", "application/pdf", as_attachment=True)

