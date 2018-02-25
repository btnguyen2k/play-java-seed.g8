#!/bin/bash

# For Production Env: Cluster enabled                                           #
# ----------------------------------------------------------------------------- #
# Start/Stop script on *NIX                                                     #
# ----------------------------------------------------------------------------- #
# Command-line arguments:                                                       #
# -h help and exist                                                             #
#    --pid <path-to-.pid-file>                                                  #
# -a|--addr <listen-address>                                                    #
# -p|--port <http-port>                                                         #
#    --https-port <https-port>                                                  #
# -m|--mem <max-memory-in-mb>                                                   #
# -c|--conf <path-to-config-file.conf>                                          #
# -l|--logconf <path-to-logback-file.xml>                                       #
#    --logdir <path-to-log-directory>, env app.logdir will be set to this value #
# -j|--jvm "extra-jvm-options"                                                  #
# --thrift-addr <listen-address>                                                #
# --thrift-port <thrift-port>                                                   #
# --thrift-ssl-port <thrift-ssl-port>                                           #
# --grpc-addr <listen-address>                                                  #
# --grpc-port <grpc-port>                                                       #
# --ssl-keystore <path-to-keystore-file>                                        #
# --ssl-keystorePassword <keystore file's password>                             #
#                                                                               #
# Cluster support command-line arguments:                                       #
# --cluster-name <cluster-name>                                                 #
# --cluster-addr <cluster-listen-address>                                       #
# --cluster-port <cluster-port>                                                 #
# --cluster-seed <seed-node-host-1:port>                                        #
# --cluster-seed <seed-node-host-2:port>                                        #
# --cluster-seed <seed-node-host-3:port>                                        #
#   etc                                                                         #
# ----------------------------------------------------------------------------- #

# from http://stackoverflow.com/questions/242538/unix-shell-script-find-out-which-directory-the-script-file-resides
pushd \$(dirname "\${0}") > /dev/null
_basedir=\$(pwd -L)
popd > /dev/null

# Setup common environment variables
# Override environment variables if needed after the next line
. \$_basedir/server-env.sh

DEFAULT_APP_MEM=128
DEFAULT_APP_CONF=application-cluster.conf
DEFAULT_APP_LOGBACK=logback-prod.xml

APP_MEM=\$DEFAULT_APP_MEM
APP_CONF=\$DEFAULT_APP_CONF
APP_LOGBACK=\$DEFAULT_APP_LOGBACK

DEFAULT_CLUSTER_ADDR="127.0.0.1"
DEFAULT_CLUSTER_PORT=`expr \$DEFAULT_APP_PORT + 7`
DEFAULT_CLUSTER_NAME=MyCluster

APP_CLUSTER_ADDR=\$DEFAULT_CLUSTER_ADDR
APP_CLUSTER_PORT=\$DEFAULT_CLUSTER_PORT
APP_CLUSTER_NAME=\$DEFAULT_CLUSTER_NAME
APP_CLUSTER_SEED=()

# Donot change this!
_CLUSTER_CONF_PREFIX_=""

buildClusterSeed() {
    FINAL_CLUSTER_SEED=()
    local INDEX=0
    while [ "\$1" != "" ]; do
        FINAL_CLUSTER_SEED+=(-D\${_CLUSTER_CONF_PREFIX_}akka.cluster.seed-nodes.\$INDEX=akka.tcp://\$APP_CLUSTER_NAME@\$1)
        INDEX=`expr \$INDEX + 1`
        shift
    done
}

doStart() {
    preStart

    buildClusterSeed \${APP_CLUSTER_SEED[@]}

    if [ "\$APP_CLUSTER_ADDR" == "" ]; then
        echo "WARN: Cluster listen address is not specified, node will not start with master role!"
    fi
    if [ "\$APP_CLUSTER_PORT" == "" -o "\$APP_CLUSTER_PORT" == "0" ]; then
        echo "WARN: Cluster port is not specified, node will not start with master role!"
    fi
    if [ "\$APP_CLUSTER_NAME" == "" ]; then
        echo "WARN: Cluster name is not specified!"
    fi
    if [ "\$FINAL_CLUSTER_SEED" == "" ]; then
        echo "WARN: No cluster seed node specified!"
    fi

    RUN_CMD=(\$APP_HOME/bin/\$APP_NAME -Dapp.home=\$APP_HOME -Dapp.logdir=\$APP_LOGDIR -Dhttp.address=\$APP_ADDR)
    if [ "\$APP_PORT" != "0" ]; then
    	RUN_CMD+=(-Dhttp.port=\$APP_PORT)
    else
        RUN_CMD+=(-Dhttp.port=disabled)
    fi
    if [ "\$APP_HTTPS_PORT" != "0" ]; then
    	RUN_CMD+=(-Dhttps.port=\$APP_HTTPS_PORT)
    	if [ "\$FINAL_APP_SSL_KEYSTORE" != "" ]; then
    		RUN_CMD+=(-Dhttps.keyStore=\$FINAL_APP_SSL_KEYSTORE -Dhttps.keyStorePassword=\$APP_SSL_KEYSTORE_PASSWORD)
    	fi
    fi
    RUN_CMD+=(-Dpidfile.path=\$APP_PID)
    if [ "\$APP_PROXY_HOST" != "" -a "\$APP_PROXY_PORT" != "0" ]; then
        RUN_CMD+=(-Dhttp.proxyHost=\$APP_PROXY_HOST -Dhttp.proxyPort=\$APP_PROXY_PORT)
        RUN_CMD+=(-Dhttps.proxyHost=\$APP_PROXY_HOST -Dhttps.proxyPort=\$APP_PROXY_PORT)
    fi
    if [ "\$APP_PROXY_USER" != "" ]; then
        RUN_CMD+=(-Dhttp.proxyUser=\$APP_PROXY_USER -Dhttp.proxyPassword=\$APP_PROXY_PASSWORD)
    fi
    if [ "\$APP_NOPROXY_HOST" != "" ]; then
        RUN_CMD+=(-Dhttp.nonProxyHosts=\$APP_NOPROXY_HOST)
    fi
    if [ "\$APP_THRIFT_PORT" != "0" -o "\$APP_THRIFT_SSL_PORT" != "0" ]; then
        RUN_CMD+=(-Dthrift.addr=\$APP_THRIFT_ADDR)
        if [ "\$APP_THRIFT_PORT" != "0" ]; then
            RUN_CMD+=(-Dthrift.port=\$APP_THRIFT_PORT)
        fi
        if [ "\$APP_THRIFT_SSL_PORT" != "0" ]; then
            RUN_CMD+=(-Dthrift.ssl_port=\$APP_THRIFT_SSL_PORT)
        fi
    fi
    if [ "\$APP_GRPC_PORT" != "0" ]; then
        RUN_CMD+=(-Dgrpc.addr=\$APP_GRPC_ADDR -Dgrpc.port=\$APP_GRPC_PORT)
    fi
    if [ "\$FINAL_APP_SSL_KEYSTORE" != "" ]; then
        RUN_CMD+=(-Djavax.net.ssl.keyStore=\$FINAL_APP_SSL_KEYSTORE -Djavax.net.ssl.keyStorePassword=\$APP_SSL_KEYSTORE_PASSWORD)
    fi
    RUN_CMD+=(-Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -J-server -J-Xms\${APP_MEM}m -J-Xmx\${APP_MEM}m)
    RUN_CMD+=(-J-XX:+UseThreadPriorities -J-XX:ThreadPriorityPolicy=42 -J-XX:+HeapDumpOnOutOfMemoryError -J-Xss256k)
    RUN_CMD+=(-J-XX:+UseTLAB -J-XX:+ResizeTLAB -J-XX:+UseNUMA -J-XX:+PerfDisableSharedMem)
    RUN_CMD+=(-J-XX:+UseG1GC -J-XX:G1RSetUpdatingPauseTimePercent=5 -J-XX:MaxGCPauseMillis=500)
    RUN_CMD+=(-J-XX:+PrintGCDetails -J-XX:+PrintGCDateStamps -J-XX:+PrintHeapAtGC -J-XX:+PrintTenuringDistribution)
    RUN_CMD+=(-J-XX:+PrintGCApplicationStoppedTime -J-XX:+PrintPromotionFailure -J-XX:PrintFLSStatistics=1)
    RUN_CMD+=(-J-Xloggc:\${APP_LOGDIR}/gc.log -J-XX:+UseGCLogFileRotation -J-XX:NumberOfGCLogFiles=10 -J-XX:GCLogFileSize=10M)
    RUN_CMD+=(-Dspring.profiles.active=production -Dconfig.file=\$FINAL_APP_CONF -Dlogger.file=\$FINAL_APP_LOGBACK)
    if [ "\$APP_CLUSTER_ADDR" != "" ]; then
        RUN_CMD+=(-D\${_CLUSTER_CONF_PREFIX_}akka.remote.netty.tcp.hostname=\$APP_CLUSTER_ADDR)
    fi
    if [ "\$APP_CLUSTER_PORT" != "" -a "\$APP_CLUSTER_PORT" != "0" ]; then
        RUN_CMD+=(-D\${_CLUSTER_CONF_PREFIX_}akka.remote.netty.tcp.port=\$APP_CLUSTER_PORT)
    fi
    if [ "\$APP_CLUSTER_NAME" != "" ]; then
        RUN_CMD+=(-D\${_CLUSTER_CONF_PREFIX_}akka.cluster.name=\$APP_CLUSTER_NAME)
    fi
    if [ "\$FINAL_CLUSTER_SEED" != "" ]; then
        RUN_CMD+=(\${FINAL_CLUSTER_SEED[@]})
    fi
    RUN_CMD+=(\$JVM_EXTRA_OPS)
    
    execStart \${RUN_CMD[@]}
    
    echo "STARTED \$APP_NAME `date`"
    
    echo "APP_ADDR            : \$APP_ADDR"
    echo "APP_PORT            : \$APP_PORT"
    echo "APP_HTTPS_PORT      : \$APP_HTTPS_PORT"
    echo "APP_THRIFT_ADDR     : \$APP_THRIFT_ADDR"
    echo "APP_THRIFT_PORT     : \$APP_THRIFT_PORT"
    echo "APP_THRIFT_SSL_PORT : \$APP_THRIFT_SSL_PORT"
    echo "APP_GRPC_ADDR       : \$APP_GRPC_ADDR"
    echo "APP_GRPC_PORT       : \$APP_GRPC_PORT"
    echo "APP_APP_SSL_KEYSTORE: \$APP_SSL_KEYSTORE"
    echo "APP_MEM             : \$APP_MEM"
    echo "APP_CONF            : \$FINAL_APP_CONF"
    echo "APP_LOGBACK         : \$FINAL_APP_LOGBACK"
    echo "APP_LOGDIR          : \$APP_LOGDIR"
    echo "APP_PID             : \$APP_PID"
    echo "JVM_EXTRA_OPS       : \$JVM_EXTRA_OPS"
    echo "APP_CLUSTER_NAME    : \$APP_CLUSTER_NAME"
    echo "APP_CLUSTER_ADDR    : \$APP_CLUSTER_ADDR"
    echo "APP_CLUSTER_PORT    : \$APP_CLUSTER_PORT"
    echo "APP_CLUSTER_SEED    : \${APP_CLUSTER_SEED[@]}"
}

usageAndExit() {
    echo "Usage: \${0##*/} <{start|stop|restart}> [-h] [--pid <.pid file>] [--logdir <log directory>] [-m <memory limit in mb>] [-a <http listen address>] [-p <http listen port>] [-c <custom config file>] [-l <custom logback config>] [-j \"<extra jvm options>\"]"
    echo "    stop   : stop the server"
    echo "    start  : start the server"
    echo "    restart: restart the server"
    echo "       -h or --help          : Display this help screen"
    echo "       -m or --mem           : JVM memory limit in mb (default \$DEFAULT_APP_MEM)"
    echo "       -a or --addr          : HTTP listen address (default \$DEFAULT_APP_ADDR)"
    echo "       -p or --port          : HTTP listen port (default \$DEFAULT_APP_PORT)"
    echo "       -c or --conf          : Custom app config file, relative file is prefixed with ./conf (default \$DEFAULT_APP_CONF)"
    echo "       -l or --logconf       : Custom logback config file, relative file is prefixed with ./conf (default \$DEFAULT_APP_LOGBACK)"
    echo "       -j or --jvm           : Extra JVM options (example: \"-Djava.rmi.server.hostname=localhost)\""
    echo "       --pid                 : Specify application's .pid file (default \$DEFAULT_APP_PID)"
    echo "       --logdir              : Specify application's log directory (default \$DEFAULT_APP_LOGDIR)"
    echo "       --thrift-addr         : Specify listen address for Thrift API Gateway (default \$DEFAULT_THRIFT_ADDR)"
    echo "       --thrift-port         : Specify listen port for Thrift API Gateway (default \$DEFAULT_THRIFT_PORT)"
    echo "       --thrift-ssl-port     : Specify listen port for Thrift API SSL Gateway (default \$DEFAULT_THRIFT_SSL_PORT)"
    echo "       --grpc-addr           : Specify listen address for gRPC API Gateway (default \$DEFAULT_GRPC_ADDR)"
    echo "       --grpc-port           : Specify listen port for gRPC API Gateway (default \$DEFAULT_GRPC_PORT)"
    echo "       --ssl-keystore        : Specify SSL keystore file (default \$DEFAULT_SSL_KEYSTORE)"
    echo "       --ssl-keystorePassword: Specify listen port for gRPC API Gateway (default \$DEFAULT_SSL_KEYSTORE_PASSWORD)"
    echo "       --cluster-name        : Specify cluster name (default \$DEFAULT_CLUSTER_NAME). If empty, cluster mode is disabled"
    echo "       --cluster-addr        : Specify cluster listen address (default \$DEFAULT_CLUSTER_ADDR). If empty, cluster mode is disabled"
    echo "       --cluster-seed        : Specify cluster seed node (format host:port)"
    echo "                               Use multiple --cluster-seed to specify more than one seed nodes. If none specified, cluster mode is disabled"
    echo
    echo "Example: start server 64mb memory limit, with custom configuration file"
    echo "    \${0##*/} start -m 64 -c abc.conf --cluster-name MyAwesomeCluster --cluster-addr 127.0.0.1 --cluster-port 9007 --cluster-seed 127.0.0.1:9007 --cluster-seed 127.0.0.1:9008"
    echo
    exit 1
}

ACTION=\$1
shift

handleUnknownParam() {
    local PARAM=\$1
    shift
    local VALUE=\$1
    shift

    case \$PARAM in
        --cluster-name)
            APP_CLUSTER_NAME=\$VALUE
            if ! [[ \$APP_CLUSTER_NAME =~ ^[a-zA-Z0-9_\.\-]+\$ ]]; then
                echo "ERROR: invalid cluster name \"\$VALUE\""
                usageAndExit
            fi
            ;;

        --cluster-addr)
            APP_CLUSTER_ADDR=\$VALUE
            ;;

        --cluster-port)
            APP_CLUSTER_PORT=\$VALUE
            if ! [[ \$VALUE =~ ^[0-9]+\$ ]]; then
                echo "ERROR: invalid cluster port \"\$VALUE\""
                usageAndExit
            fi
            ;;

        --cluster-seed)
            if ! [[ \$VALUE =~ ^.+:[0-9]+\$ ]]; then
                echo "ERROR: invalid cluster seed \"\$VALUE\""
                usageAndExit
            fi
            APP_CLUSTER_SEED+=(\$VALUE)
            ;;

        *)
            echo "ERROR: unknown parameter \"\$PARAM\""
            usageAndExit
            ;;
    esac
}

while [ "\$1" != "" ]; do
    PARAM=\$1
    shift
    VALUE=\$1
    shift

    parseParam \$PARAM \$VALUE
done

doAction \$ACTION
