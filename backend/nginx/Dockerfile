FROM nginx:1.23-alpine

RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d

RUN apk update && apk add \
    openssl

COPY generate_cert_and_key.sh generate_cert_and_key.sh
RUN chmod +x generate_cert_and_key.sh && ./generate_cert_and_key.sh
