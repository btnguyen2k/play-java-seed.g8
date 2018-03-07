#!/bin/sh

## For dev env only!
## Start application in cluster mode: 1st node (roles = [Role1])

#unset SBT_OPTS
sbt -Dconfig.file=conf/application-cluster.conf -Dlogger.file=conf/logback-dev.xml \
	-Dhttp.port=9001 -Dthrift.port=0 -Dthrift.ssl_port=0 -Dgrpc.port=0 \
	-Dplay.akka.actor-system=MyCluster -Dakka.cluster.name=MyCluster -Dakka.remote.netty.tcp.hostname=127.0.0.1 -Dakka.remote.netty.tcp.port=9051 \
	-Dakka.cluster.roles.0=master -Dakka.cluster.roles.1=Role1 \
	-Dakka.cluster.seed-nodes.0=akka.tcp://MyCluster@127.0.0.1:9051 \
	-Dakka.cluster.seed-nodes.1=akka.tcp://MyCluster@127.0.0.1:9052 \
	run
