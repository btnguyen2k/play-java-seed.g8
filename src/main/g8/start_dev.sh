#!/bin/sh

## Start application in dev mode with remote debugging

#unset SBT_OPTS
sbt compile && sbt -jvm-debug 9999 -Dhttp.port=9000 -Dhttps.port=9043 \
	-Dconfig.file=conf/application.conf -Dlogger.file=conf/logback-dev.xml \
	-Dplay.server.https.keyStore.path=conf/keys/server.keystore \
	-Dplay.server.https.keyStore.password=pl2yt3mpl2t3 \
	-Dhttp2.enabled=true -Dplay.server.akka.http2.enabled=true \
	run
