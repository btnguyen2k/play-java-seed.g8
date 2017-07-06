#!/bin/sh

# Sample script to generate self-signed server's keystore & client's truststore files.
# By Thanh Nguyen <btnguyen2k@gmail.com>
# Since template-v0.1.4

echo Generating keystore server.keystore file...
if [ -f server.keystore ]; then
    echo File server.keystore already existed!
    exit 1
fi
keytool -genkey -noprompt -trustcacerts -keyalg RSA -alias playtemplate -dname "CN=Play Template, OU=com.github.btnguyen2k, O=btnguyen2k, L=HCM, ST=HCM, C=VN" -keypass pl2yt3mpl2t3 -keystore server.keystore -storepass pl2yt3mpl2t3

echo Exporting server.cer from server.keystore file...
if [ -f server.cer ]; then
    echo File server.cer already existed!
    exit 1
fi
keytool -export -alias playtemplate -storepass pl2yt3mpl2t3 -file server.cer -keystore server.keystore

echo Generate truststore client.truststore file...
if [ -f client.truststore ]; then
    echo File client.truststore already existed!
    exit 1
fi
echo yes | keytool -import -v -trustcacerts -alias playtemplate -file server.cer -keystore client.truststore -keypass pl2yt3mpl2t3 -storepass pl2yt3mpl2t3
