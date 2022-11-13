#!/bin/bash

cd .. 

export FLASK_APP=backend

rm -rf instance

flask init-db

flask testgen

python3 -m pytest --cov=backend --cov-report term-missing backend/tests
