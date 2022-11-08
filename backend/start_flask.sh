#!/bin/bash

# Move one folder back to use flask commands on package
#cd ..

# Initialize database
python3 -m flask init-db

# Fill the database
python3 -m flask testgen

gunicorn --bind 0.0.0.0:4000 "backend:create_app()"
#python3 -m flask run
