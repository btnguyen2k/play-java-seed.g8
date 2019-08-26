#!/bin/bash

# For Non-Production Env                                                        #
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
# ----------------------------------------------------------------------------- #

# from http://stackoverflow.com/questions/242538/unix-shell-script-find-out-which-directory-the-script-file-resides
pushd \$(dirname "\${0}") > /dev/null
_basedir=\$(pwd -L)
popd > /dev/null

# Setup common environment variables
# Override environment variables if needed after the next line
. \$_basedir/server-env.sh

DEFAULT_APP_MEM=64
DEFAULT_APP_CONF=application.conf
DEFAULT_APP_LOGBACK=logback-dev.xml

APP_MEM=\$DEFAULT_APP_MEM
APP_CONF=\$DEFAULT_APP_CONF
APP_LOGBACK=\$DEFAULT_APP_LOGBACK

doStart() {
    preStart

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

    RUN_CMD+=(-Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -J-server -J-Xms\${APP_MIN_MEM}m -J-Xmx\${APP_MEM}m)
    RUN_CMD+=(-J-XX:+ExitOnOutOfMemoryError -J-XX:+CrashOnOutOfMemoryError)
    RUN_CMD+=(-J-Xlog:gc -J-XX:+UseSerialGC -J-XX:MinHeapFreeRatio=5 -J-XX:MaxHeapFreeRatio=10 -J-XX:-ShrinkHeapInSteps)
    RUN_CMD+=(-Dspring.profiles.active=development -Dconfig.file=\$FINAL_APP_CONF -Dlogger.file=\$FINAL_APP_LOGBACK)
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
    echo "APP_APP_SSL_KEYSTORE: \$FINAL_APP_SSL_KEYSTORE"
    echo "APP_MEM             : \$APP_MEM"
    echo "APP_CONF            : \$FINAL_APP_CONF"
    echo "APP_LOGBACK         : \$FINAL_APP_LOGBACK"
    echo "APP_LOGDIR          : \$APP_LOGDIR"
    echo "APP_PID             : \$APP_PID"
    echo "JVM_EXTRA_OPS       : \$JVM_EXTRA_OPS"
}

ACTION=\$1
shift

while [ "\$1" != "" ]; do
    PARAM=\$1
    shift
    VALUE=\$1
    shift

    parseParam \$PARAM \$VALUE
done

doAction \$ACTION
