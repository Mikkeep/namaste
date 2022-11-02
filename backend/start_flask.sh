#!/bin/bash

# Move one folder back to use flask commands on package
#cd ..

# Initialize database
python3 -m flask init-db

# Fill the database
python3 -m flask testgen

python3 -m flask run
