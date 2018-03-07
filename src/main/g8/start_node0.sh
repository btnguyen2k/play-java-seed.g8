#!/bin/sh

## For dev env only!
## Start application in cluster (single node) mode with remote debugging

#unset SBT_OPTS
sbt -jvm-debug 9999 -Dconfig.file=conf/application-cluster.conf -Dlogger.file=conf/logback-dev.xml \
	-Dhttp.port=9000 -Dhttps.port=9043 -Dthrift.port=0 -Dthrift.ssl_port=0 -Dgrpc.port=0 \
	-Dplay.server.https.keyStore.path=conf/keys/server.keystore \
	-Dplay.server.https.keyStore.password=pl2yt3mpl2t3 \
	-Dhttp2.enabled=true -Dplay.server.akka.http2.enabled=true \
	-Dplay.akka.actor-system=MyCluster -Dakka.cluster.name=MyCluster -Dakka.remote.netty.tcp.hostname=127.0.0.1 -Dakka.remote.netty.tcp.port=9051 \
	-Dakka.cluster.roles.0=master \
	-Dakka.cluster.seed-nodes.0=akka.tcp://MyCluster@127.0.0.1:9051 \
	run
