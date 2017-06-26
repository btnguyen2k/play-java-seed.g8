#!/bin/bash

# For Production Env                                                           #
# ---------------------------------------------------------------------------- #
# Start/Stop script on *NIX                                                    #
# ---------------------------------------------------------------------------- #
# Command-line arguments:                                                      #
# -h help and exist                                                            #
#    --pid <path-to-.pid-file>                                                 #
# -a|--addr <listen-address>                                                   #
# -p|--port <http-port>                                                        #
# -m|--mem <max-memory-in-mb>                                                  #
# -c|--conf <path-to-config-file.conf>                                         #
# -l|--logconf <path-to-logback-file.xml>                                      #
#    --logdir <path-to-log-directory, env app.logdir will be set to this value #
# -j|--jvm "extra-jvm-options"                                                 #
# --thrift-addr <listen-address>                                               #
# --thrift-port <thrift-port>                                                  #
# --thrift-ssl-port <thrift-ssl-port>                                          #
# --grpc-addr <listen-address>                                                 #
# --grpc-port <thrift-port>                                                    #
# --ssl-keystore <path-to-keystore-file>                                       #
# --ssl-keystorePassword <keystore file's password>                            #
# ---------------------------------------------------------------------------- #

# from http://stackoverflow.com/questions/242538/unix-shell-script-find-out-which-directory-the-script-file-resides
pushd \$(dirname "\${0}") > /dev/null
_basedir=\$(pwd -L)
popd > /dev/null

APP_HOME=\$_basedir/..
APP_NAME=$name;format="normalize"$

# Setup proxy if needed
#APP_PROXY_HOST=host
#APP_PROXY_PORT=port
#APP_NOPROXY_HOST="localhost|127.0.0.1|10.*|192.168.*|*.local|host.domain.com"
#APP_PROXY_USER=user
#APP_PROXY_PASSWORD=password

DEFAULT_APP_ADDR=0.0.0.0
DEFAULT_APP_PORT=9000
DEFAULT_APP_MEM=128
DEFAULT_APP_CONF=application-prod.conf
DEFAULT_APP_LOGBACK=logback-prod.xml
DEFAULT_APP_PID=\$APP_HOME/\$APP_NAME.pid
DEFAULT_APP_LOGDIR=\$APP_HOME/logs

# Thrift API Gateway listen address, default: same as DEFAULT_APP_ADDR
DEFAULT_THRIFT_ADDR=\$DEFAULT_APP_ADDR
# Thrift API Gateway port, default: DEFAULT_APP_PORT+5
DEFAULT_THRIFT_PORT=`expr \$DEFAULT_APP_PORT + 5`
# Thrift API Gateway SSL port, default DEFAULT_THRIFT_PORT+22
DEFAULT_THRIFT_SSL_PORT=`expr \$DEFAULT_THRIFT_PORT + 22`

# gRPC API Gateway listen address, default: same as DEFAULT_APP_ADDR
DEFAULT_GRPC_ADDR=\$DEFAULT_APP_ADDR
# gRPC API Gateway port, default DEFAULT_APP_PORT+10
DEFAULT_GRPC_PORT=`expr \$DEFAULT_APP_PORT + 10`

# Default keystore file for SSL
DEFAULT_SSL_KEYSTORE=\$APP_HOME/conf/keys/server.keystore
# Default keystore password for SSL
DEFAULT_SSL_KEYSTORE_PASSWORD=pl2yt3mpl2t3

APP_ADDR=\$DEFAULT_APP_ADDR
APP_PORT=\$DEFAULT_APP_PORT
APP_MEM=\$DEFAULT_APP_MEM
APP_CONF=\$DEFAULT_APP_CONF
APP_LOGBACK=\$DEFAULT_APP_LOGBACK
APP_PID=\$DEFAULT_APP_PID
APP_LOGDIR=\$DEFAULT_APP_LOGDIR

APP_THRIFT_ADDR=\$DEFAULT_THRIFT_ADDR
APP_THRIFT_PORT=\$DEFAULT_THRIFT_PORT
APP_THRIFT_SSL_PORT=\$DEFAULT_THRIFT_SSL_PORT
APP_GRPC_ADDR=\$DEFAULT_GRPC_ADDR
APP_GRPC_PORT=\$DEFAULT_GRPC_PORT
APP_SSL_KEYSTORE=\$DEFAULT_SSL_KEYSTORE
APP_SSL_KEYSTORE_PASSWORD=\$DEFAULT_SSL_KEYSTORE_PASSWORD

JVM_EXTRA_OPS=

isRunning() {
    local PID=\$(cat "\$1" 2>/dev/null) || return 1
    kill -0 "\$PID" 2>/dev/null
}

doStop() {
    echo -n "Stopping \$APP_NAME: "

    if isRunning \$APP_PID; then
        local PID=\$(cat "\$APP_PID" 2>/dev/null)
        kill "\$PID" 2>/dev/null
        
        TIMEOUT=30
        while isRunning \$APP_PID; do
            if (( TIMEOUT-- == 0 )); then
                kill -KILL "\$PID" 2>/dev/null
            fi
            sleep 1
        done
        
        rm -f "\$APP_PID"
    fi
    
    echo OK
}

doStart() {
    echo -n "Starting \$APP_NAME: "
    
    if [ "\$APP_PID" == "" ]; then   
        echo "Error: PID file not specified!"
        exit 1
    fi
    if [ -f "\$APP_PID" ]; then
        if isRunning \$APP_PID; then
            echo "Already running!"
            exit 1
        else
            # dead pid file - remove
            rm -f "\$APP_PID"
        fi
    fi

    if [ "\$APP_LOGDIR" == "" ]; then    
        echo "Error: Log directory not specified!"
        exit 1
    else
        mkdir -p \$APP_LOGDIR
        if [ ! -d \$APP_LOGDIR ]; then
            echo "Error: Log directory \$APP_LOGDIR cannot be created or not a writable directory!"
        fi
    fi
    
    if [ "\$APP_ADDR" == "" ]; then	
    	echo "Error: HTTP listen address not specified!"
        exit 1
    fi

    if [ "\$APP_PORT" == "" ]; then	
    	echo "Error: HTTP listen port not specified!"
        exit 1
    fi

    _startsWithSlash_='^\/.*\$'

    if [ "\$APP_CONF" == "" ]; then	
    	echo "Error: Application configuration file not specified!"
        exit 1
    else
        if [[ \$APP_CONF =~ \$_startsWithSlash_ ]]; then
            FINAL_APP_CONF=\$APP_CONF
        else
            FINAL_APP_CONF=\$APP_HOME/conf/\$APP_CONF
        fi

        if [ ! -f "\$FINAL_APP_CONF" ]; then
            echo "Error: Application configuration file not found: \$FINAL_APP_CONF"
            exit 1
        fi
    fi
    
    if [ "\$APP_LOGBACK" == "" ]; then
    	echo "Error: Application logback config file not specified!"
        exit 1
    else
        if [[ \$APP_LOGBACK =~ \$_startsWithSlash_ ]]; then
            FINAL_APP_LOGBACK=\$APP_LOGBACK
        else
            FINAL_APP_LOGBACK=\$APP_HOME/conf/\$APP_LOGBACK
        fi

        if [ ! -f "\$FINAL_APP_LOGBACK" ]; then
        	echo "Error: Application logback config file not found: \$FINAL_APP_LOGBACK"
            exit 1
        fi
    fi
    
    RUN_CMD=(\$APP_HOME/bin/\$APP_NAME -Dapp.home=\$APP_HOME -Dapp.logdir=\$APP_LOGDIR -Dhttp.port=\$APP_PORT -Dhttp.address=\$APP_ADDR)
    RUN_CMD+=(-Dpidfile.path=\$APP_PID)
    RUN_CMD+=(-Dakka.log-config-on-start=true)
    if [ "\$APP_PROXY_HOST" != "" -a "\$APP_PROXY_PORT" != "" ]; then
        RUN_CMD+=(-Dhttp.proxyHost=\$APP_PROXY_HOST -Dhttp.proxyPort=\$APP_PROXY_PORT)
        RUN_CMD+=(-Dhttps.proxyHost=\$APP_PROXY_HOST -Dhttps.proxyPort=\$APP_PROXY_PORT)
    fi
    if [ "\$APP_PROXY_USER" != "" ]; then
        RUN_CMD+=(-Dhttp.proxyUser=\$APP_PROXY_USER -Dhttp.proxyPassword=\$APP_PROXY_PASSWORD)
    fi
    if [ "\$APP_NOPROXY_HOST" != "" ]; then
        RUN_CMD+=(-Dhttp.nonProxyHosts=\$APP_NOPROXY_HOST)
    fi
    if [ "\$APP_THRIFT_PORT" != "" -o "\$APP_THRIFT_SSL_PORT" != "" ]; then
        RUN_CMD+=(-Dthrift.addr=\$APP_THRIFT_ADDR -Dthrift.port=\$APP_THRIFT_PORT -Dthrift.ssl_port=\$APP_THRIFT_SSL_PORT)
    fi
    if [ "\$APP_GRPC_PORT" != "" ]; then
        RUN_CMD+=(-Dgrpc.addr=\$APP_GRPC_ADDR -Dgrpc.port=\$APP_GRPC_PORT)
    fi
    if [ "\$APP_SSL_KEYSTORE" != "" ]; then
        RUN_CMD+=(-Djavax.net.ssl.keyStore=\$APP_SSL_KEYSTORE -Djavax.net.ssl.keyStorePassword=\$APP_SSL_KEYSTORE_PASSWORD)
    fi
    RUN_CMD+=(-Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -J-server -J-Xms\${APP_MEM}m -J-Xmx\${APP_MEM}m)
    RUN_CMD+=(-J-XX:+UseThreadPriorities -J-XX:ThreadPriorityPolicy=42 -J-XX:+HeapDumpOnOutOfMemoryError -J-Xss256k)
    RUN_CMD+=(-J-XX:+UseTLAB -J-XX:+ResizeTLAB -J-XX:+UseNUMA -J-XX:+PerfDisableSharedMem)
    RUN_CMD+=(-J-XX:+UseG1GC -J-XX:G1RSetUpdatingPauseTimePercent=5 -J-XX:MaxGCPauseMillis=500)
    RUN_CMD+=(-J-XX:+PrintGCDetails -J-XX:+PrintGCDateStamps -J-XX:+PrintHeapAtGC -J-XX:+PrintTenuringDistribution)
    RUN_CMD+=(-J-XX:+PrintGCApplicationStoppedTime -J-XX:+PrintPromotionFailure -J-XX:PrintFLSStatistics=1)
    RUN_CMD+=(-J-Xloggc:\${APP_HOME}/logs/gc.log -J-XX:+UseGCLogFileRotation -J-XX:NumberOfGCLogFiles=10 -J-XX:GCLogFileSize=10M)
    RUN_CMD+=(-Dspring.profiles.active=production -Dconfig.file=\$FINAL_APP_CONF -Dlogger.file=\$FINAL_APP_LOGBACK)
    RUN_CMD+=(\$JVM_EXTRA_OPS)
    
    "\${RUN_CMD[@]}" &
    disown \$!
    #echo \$! > "\$APP_PID"
    
    echo "STARTED \$APP_NAME `date`"
    
    echo "APP_ADDR            : \$APP_ADDR"
    echo "APP_PORT            : \$APP_PORT"
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
    echo
    echo "Example: start server 64mb memory limit, with custom configuration file"
    echo "    \${0##*/} start -m 64 -c abc.conf"
    echo
    exit 1
}

ACTION=\$1
shift

# parse parameters: see https://gist.github.com/jehiah/855086
_number_='^[0-9]+\$'
while [ "\$1" != "" ]; do
    PARAM=\$1
    shift
    VALUE=\$1
    shift

    case \$PARAM in
        -h|--help)
            usageAndExit
            ;;

        --pid)
            APP_PID=\$VALUE
            ;;

        -m|--mem)
            APP_MEM=\$VALUE
            if ! [[ \$APP_MEM =~ \$_number_ ]]; then
                echo "ERROR: invalid memory value \"\$APP_MEM\""
                usageAndExit
            fi
            ;;

        -a|--addr)
            APP_ADDR=\$VALUE
            ;;

        -p|--port)
            APP_PORT=\$VALUE
            if ! [[ \$APP_PORT =~ \$_number_ ]]; then
                echo "ERROR: invalid HTTP port number \"\$APP_PORT\""
                usageAndExit
            fi
            ;;

        -c|--conf)
            APP_CONF=\$VALUE
            ;;
            
        -l|--logconf)
            APP_LOGBACK=\$VALUE
            ;;

        --logdir)
            APP_LOGDIR=\$VALUE
            ;;

        -j)
            JVM_EXTRA_OPS=\$VALUE
            ;;

        --thrift-addr|--thriftAddr)
            APP_THRIFT_ADDR=\$VALUE
            ;;
            
        --thrift-port|--thriftPort)
            APP_THRIFT_PORT=\$VALUE
            if ! [[ \$APP_THRIFT_PORT =~ \$_number_ ]]; then
                echo "ERROR: invalid Thrift port number \"\$APP_THRIFT_PORT\""
                usageAndExit
            fi
            ;;

        --thrift-ssl-port|--thriftSslPort)
            APP_THRIFT_SSL_PORT=\$VALUE
            if ! [[ \$APP_THRIFT_SSL_PORT =~ \$_number_ ]]; then
                echo "ERROR: invalid Thrift SSL port number \"\$APP_THRIFT_SSL_PORT\""
                usageAndExit
            fi
            ;;

        --grpc-addr|--grpcAddr)
            APP_GRPC_ADDR=\$VALUE
            ;;
            
        --grpc-port|--grpcPort)
            APP_GRPC_PORT=\$VALUE
            if ! [[ \$APP_GRPC_PORT =~ \$_number_ ]]; then
                echo "ERROR: invalid gRPC port number \"\$APP_GRPC_PORT\""
                usageAndExit
            fi
            ;;

        --ssl-keystore|--ssl-keyStore|--sslKeyStore|--sslKeystore)
            APP_SSL_KEYSTORE=\$VALUE
            ;;

        --ssl-keystorePassword|--ssl-keyStorePassword|--sslKeyStorePassword|--sslKeystorePassword|--ssl-keystore-password|--ssl-keyStore-password)
            APP_SSL_KEYSTORE_PASSWORD=\$VALUE
            ;;

        *)
            echo "ERROR: unknown parameter \"\$PARAM\""
            usageAndExit
            ;;
    esac
done

case "\$ACTION" in
    stop)
        doStop
        ;;

    start)
        doStart
        ;;

    restart)
        doStop
        doStart
        ;;

    *)
        usageAndExit
        ;;
esac
