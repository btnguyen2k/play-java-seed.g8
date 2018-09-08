#!/bin/sh

# Sample script to generate self-signed keystore, certificate and truststore files
# By Thanh Nguyen <btnguyen2k@gmail.com>
# Since template-v2.6.r8

ALIAS=playtemplate
PASSWORD=pl2yt3mpl2t3

echo Generating server.keystore file...
keytool -genkey -noprompt -trustcacerts -keyalg RSA -alias $ALIAS -dname "CN=localhost, OU=com.github.btnguyen2k, O=DDTH, L=HCM, ST=HCM, C=VN" -keypass $PASSWORD -keystore server.keystore -storepass $PASSWORD

echo Exporting server.cer from server.keystore file...
keytool -export -alias $ALIAS -storepass $PASSWORD -file server-temp.cer -keystore server.keystore

echo Generate client.truststore file...
echo yes | keytool -import -v -trustcacerts -alias $ALIAS -file server-temp.cer -keystore client.truststore -keypass $PASSWORD -storepass $PASSWORD
rm -f server-temp.cer

echo Generating X509 server-grpc.key and server-grpc.key...
openssl req -x509 -newkey rsa:4096 -keyout server-grpc-nodes.key -out server-grpc.cer -days 365 -nodes -subj '/CN=localhost'

echo Generating server.p12, server.cer and server.key/server-nodes.key
keytool -importkeystore -srckeystore server.keystore -srcalias $ALIAS -srcstorepass $PASSWORD -destkeystore server.p12 -deststoretype PKCS12 -deststorepass $PASSWORD -destkeypass $PASSWORD
openssl pkcs12 -in server.p12 -nodes -nocerts -out server-nodes.key -password pass:$PASSWORD
openssl pkcs12 -in server.p12 -nocerts -out server.key -password pass:$PASSWORD -passout pass:$PASSWORD
openssl pkcs12 -in server.p12 -nokeys -out server.cer -password pass:$PASSWORD
