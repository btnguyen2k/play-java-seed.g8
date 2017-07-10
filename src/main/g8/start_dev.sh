#!/bin/sh

unset SBT_OPTS && sbt stage
export SBT_AGENT=$(pwd)/$(find target -name 'jetty-alpn-agent-*.jar' | head -1)
export SBT_OPTS="-javaagent:$SBT_AGENT"
sbt -jvm-debug 9999 -Dhttp.port=9000 -Dhttps.port=9022 \
	-Dconfig.file=conf/application.conf -Dlogger.file=conf/logback-dev.xml \
	-Dplay.server.https.keyStore.path=conf/keys/server.keystore \
	-Dplay.server.https.keyStore.password=pl2yt3mpl2t3 \
	-Dplay.server.akka.http2.enabled=true \
	run
