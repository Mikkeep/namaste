# syntax=docker/dockerfile:1

FROM python:3.8-slim-buster

WORKDIR /backend
COPY . backend/

ENV FLASK_APP=backend
ENV FLASK_RUN_HOST=0.0.0.0
ENV FLASK_DEBUG=True

COPY requirements.txt ./
COPY start_flask.sh ./

RUN pip3 install -r requirements.txt

CMD [ "./start_flask.sh" ]
