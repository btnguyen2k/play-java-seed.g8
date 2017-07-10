#!/bin/sh
# For dev env only!

unset SBT_OPTS
sbt -jvm-debug 9999 -Dconfig.file=conf/application-cluster.conf -Dlogger.file=conf/logback-dev.xml \
	-Dhttp.port=9000 -Dthrift.port=0 -Dthrift.ssl_port=0 -Dgrpc.port=0 \
	-Dplay.akka.actor-system=MyCluster -Dakka.cluster.name=MyCluster \
	-Dakka.cluster.seed-nodes.0=akka.tcp://MyCluster@127.0.0.1:9051 \
	run
