""" Methods for Files """

from flask import send_file
import platform
from flask import request, Response, session, send_from_directory, send_file
from flask_restful import Resource
from ..constants import JSON


class GetFile(Resource):
    
    def get(self):
        if platform.system() == "Linux" or platform.system() == "Darwin":
            return send_file("./resources/file.pdf", "application/pdf", as_attachment=True)
        elif platform.system() == "Windows":
            return send_file(".\\resources\\file.pdf", "application/pdf", as_attachment=True)
