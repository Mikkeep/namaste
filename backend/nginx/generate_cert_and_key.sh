#!/bin/sh

#Make sure target folder exists
[ ! -d "/etc/ssl/app" ] && \
    echo "Generating directory" && \
    mkdir /etc/ssl/app

#Generate key and certificate
#Subj is for adding Common Name to the certificate, other parameters are not needed
openssl req -x509 -newkey rsa:4096 -nodes \
    -keyout /etc/ssl/app/privkey.key \
    -out /etc/ssl/app/certificate.pem \
    -sha256 -days 365 -subj '/CN=localhost'
