#!/bin/sh
# For dev env only!

sbt -jvm-debug 9999 -Dplay.server.netty.transport=jdk -Dconfig.file=./conf/application-cluster.conf \
	-Dhttp.port=9001 -Dthrift.port=0 -Dthrift.ssl_port=0 -Dgrpc.port=0 \
	-Dcluster_conf.akka.remote.netty.tcp.port=9051 \
	run
	#-Dcluster_conf.akka.cluster.seed-nodes.0=akka.tcp://MyCluster@127.0.0.1:9051 \
	#run

