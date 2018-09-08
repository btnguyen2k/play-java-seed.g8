#!/bin/sh

## For dev env only!
## Start application in cluster mode: 1st node (roles = [Role1])
## Note: Thrift & gRPC API Servers are disabled

#unset SBT_OPTS
AKKA_CLUSTER_NAME=play-dev-mode
CONF_PREFIX=play.akka.dev-mode
sbt -Dconfig.file=conf/application-cluster.conf -Dlogger.file=conf/logback-dev.xml \
	-Dhttp.port=9001 -Dhttps.port=0 -Dthrift.port=0 -Dthrift.ssl_port=0 -Dgrpc.port=0 -Dgrpc.ssl_port=0 \
	-Dakka.cluster.jmx.multi-mbeans-in-same-jvm=on \
	-D$CONF_PREFIX.play.akka.actor-system=$AKKA_CLUSTER_NAME -Dplay.akka.actor-system=$AKKA_CLUSTER_NAME \
	-D$CONF_PREFIX.akka.cluster.name=$AKKA_CLUSTER_NAME -Dakka.cluster.name=$AKKA_CLUSTER_NAME \
	-D$CONF_PREFIX.akka.remote.netty.tcp.hostname=127.0.0.1 -D$CONF_PREFIX.akka.remote.netty.tcp.port=2551 \
	-Dakka.remote.netty.tcp.hostname=127.0.0.1 -Dakka.remote.netty.tcp.port=9051 \
	-D$CONF_PREFIX.akka.cluster.roles.0=master -Dakka.cluster.roles.0=master \
	-D$CONF_PREFIX.akka.cluster.roles.1=Role1 -Dakka.cluster.roles.1=Role1 \
	-D$CONF_PREFIX.akka.cluster.seed-nodes.0=akka.tcp://$AKKA_CLUSTER_NAME@127.0.0.1:2551 -Dakka.cluster.seed-nodes.0=akka.tcp://$AKKA_CLUSTER_NAME@127.0.0.1:9051 \
	-D$CONF_PREFIX.akka.cluster.seed-nodes.1=akka.tcp://$AKKA_CLUSTER_NAME@127.0.0.1:2552 -Dakka.cluster.seed-nodes.1=akka.tcp://$AKKA_CLUSTER_NAME@127.0.0.1:9052 \
	run
