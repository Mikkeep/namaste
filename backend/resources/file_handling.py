""" Methods for Files """

from flask import request, Response, session, send_from_directory, send_file
from flask_restful import Resource
from ..constants import JSON


class getFile(Resource):
    
    def get(self):
        return send_file("./file.pdf", "application/pdf", as_attachment=True)
