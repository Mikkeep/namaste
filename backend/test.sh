#!/bin/bash

cd .. 

pytest --cov=backend --cov-report term-missing
