#!/bin/sh
# For dev env only!

sbt -Dplay.server.netty.transport=jdk -Dconfig.file=./conf/application-cluster.conf \
	-Dhttp.port=9003 -Dthrift.port=0 -Dthrift.ssl_port=0 -Dgrpc.port=0\
	-Dcluster_conf.akka.remote.netty.tcp.port=9053 \
	-Dcluster_conf.akka.cluster.seed-nodes.0=akka.tcp://MyCluster@127.0.0.1:9051 -Dcluster_conf.akka.cluster.seed-nodes.1=akka.tcp://MyCluster@127.0.0.1:9052 \
	run
