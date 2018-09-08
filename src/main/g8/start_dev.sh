#!/bin/sh

## For dev env only!
## Start application in dev mode with remote debugging

#unset SBT_OPTS
sbt -jvm-debug 9999 -Dhttp.port=9000 -Dhttps.port=9043 \
	-Dconfig.file=conf/application.conf -Dlogger.file=conf/logback-dev.xml \
	-Dplay.server.https.keyStore.path=conf/keys/server.keystore \
	-Dplay.server.https.keyStore.password=pl2yt3mpl2t3 \
	-Dhttp2.enabled=true -Dplay.server.akka.http2.enabled=true \
	-Dthrift.port=9090 -Dthrift.ssl_port=9093 -Dthrift.ssl.keyStore=conf/keys/server.keystore -Dthrift.ssl.keyStorePassword=pl2yt3mpl2t3 \
	-Dgrpc.port=9095 -Dgrpc.ssl_port=9098 -Dgrpc.ssl.certChain=conf/keys/server-grpc.cer -Dgrpc.ssl.privKey=conf/keys/server-grpc-nodes.key \
	run
