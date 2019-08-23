#!/bin/bash

# Common Environment Settings #

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
DEFAULT_APP_HTTPS_PORT=`expr \$DEFAULT_APP_PORT + 43`
DEFAULT_APP_MIN_MEM=16
DEFAULT_APP_MEM=64
DEFAULT_APP_CONF=application.conf
DEFAULT_APP_LOGBACK=logback-dev.xml
DEFAULT_APP_PID=\$APP_HOME/\$APP_NAME.pid
DEFAULT_APP_LOGDIR=\$APP_HOME/logs

# Thrift API Gateway listen address, default: same as DEFAULT_APP_ADDR
DEFAULT_THRIFT_ADDR=\$DEFAULT_APP_ADDR
# Thrift API Gateway port, default: DEFAULT_APP_PORT+5
DEFAULT_THRIFT_PORT=`expr \$DEFAULT_APP_PORT + 5`
# Thrift API Gateway SSL port, default DEFAULT_THRIFT_PORT+43
DEFAULT_THRIFT_SSL_PORT=`expr \$DEFAULT_THRIFT_PORT + 43`

# gRPC API Gateway listen address, default: same as DEFAULT_APP_ADDR
DEFAULT_GRPC_ADDR=\$DEFAULT_APP_ADDR
# gRPC API Gateway port, default DEFAULT_APP_PORT+10
DEFAULT_GRPC_PORT=`expr \$DEFAULT_APP_PORT + 10`

# Default keystore file for SSL (HTTPS & Thrift SSL)
DEFAULT_SSL_KEYSTORE=keys/server.keystore
# Default keystore password for SSL
DEFAULT_SSL_KEYSTORE_PASSWORD=pl2yt3mpl2t3

APP_ADDR=\$DEFAULT_APP_ADDR
APP_PORT=\$DEFAULT_APP_PORT
APP_HTTPS_PORT=\$DEFAULT_APP_HTTPS_PORT
APP_MIN_MEM=\$DEFAULT_APP_MIN_MEM
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

preStart() {
    if [ "\$APP_PID" == "" ]; then
        echo "ERROR: PID file not specified!"
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
        echo "ERROR: Log directory not specified!"
        exit 1
    else
        mkdir -p \$APP_LOGDIR
        if [ ! -d \$APP_LOGDIR ]; then
            echo "ERROR: Log directory \$APP_LOGDIR cannot be created or not a writable directory!"
        fi
    fi

    if [ "\$APP_ADDR" == "" ]; then
        echo "ERROR: HTTP listen address not specified!"
        exit 1
    fi

    if [ "\$APP_PORT" == "" ]; then
        echo "ERROR: HTTP listen port not specified!"
        exit 1
    fi

    local _startsWithSlash_='^\/.*\$'

    if [ "\$APP_CONF" == "" ]; then
        echo "ERROR: Application configuration file not specified!"
        exit 1
    else
        if [[ \$APP_CONF =~ \$_startsWithSlash_ ]]; then
            FINAL_APP_CONF=\$APP_CONF
        else
            FINAL_APP_CONF=\$APP_HOME/conf/\$APP_CONF
        fi

        if [ ! -f "\$FINAL_APP_CONF" ]; then
            echo "ERROR: Application configuration file not found: \$FINAL_APP_CONF"
            exit 1
        fi
    fi

    if [ "\$APP_LOGBACK" == "" ]; then
        echo "ERROR: Application logback config file not specified!"
        exit 1
    else
        if [[ \$APP_LOGBACK =~ \$_startsWithSlash_ ]]; then
            FINAL_APP_LOGBACK=\$APP_LOGBACK
        else
            FINAL_APP_LOGBACK=\$APP_HOME/conf/\$APP_LOGBACK
        fi

        if [ ! -f "\$FINAL_APP_LOGBACK" ]; then
            echo "ERROR: Application logback config file not found: \$FINAL_APP_LOGBACK"
            exit 1
        fi
    fi

    if [ "\$APP_SSL_KEYSTORE" != "" ]; then
        if [[ \$APP_SSL_KEYSTORE =~ \$_startsWithSlash_ ]]; then
            FINAL_APP_SSL_KEYSTORE=\$APP_SSL_KEYSTORE
        else
            FINAL_APP_SSL_KEYSTORE=\$APP_HOME/conf/\$APP_SSL_KEYSTORE
        fi

        if [ ! -f "\$FINAL_APP_SSL_KEYSTORE" ]; then
            echo "ERROR: SSL Keystore file not found: \$FINAL_APP_SSL_KEYSTORE"
            exit 1
        fi
    fi

    if [ "\$APP_PORT" == "" ]; then
        APP_PORT=0
    fi

    if [ "\$APP_HTTPS_PORT" == "" ]; then
        APP_HTTPS_PORT=0
    fi

    if [ "\$APP_PROXY_PORT" == "" ]; then
        APP_PROXY_PORT=0
    fi

    if [ "\$APP_THRIFT_PORT" == "" ]; then
        APP_THRIFT_PORT=0
    fi

    if [ "\$APP_THRIFT_SSL_PORT" == "" ]; then
        APP_THRIFT_SSL_PORT=0
    fi

    if [ "\$APP_GRPC_PORT" == "" ]; then
        APP_GRPC_PORT=0
    fi
}

execStart() {
    local CMD=(\$1)
    shift
    while [ "\$1" != "" ]; do
        CMD+=(\$1)
        shift
    done

    echo -n "Starting \$APP_NAME on `date`..."

    "\${CMD[@]}" &
    disown \$!
}

execStartForeground() {
    local CMD=(\$1)
    shift
    while [ "\$1" != "" ]; do
        CMD+=(\$1)
        shift
    done

    echo -n "Starting \$APP_NAME on `date`..."

    "\${CMD[@]}"
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
    echo "       --https-port          : Specify listen port for HTTPS & HTTP/2 (default \$DEFAULT_HTTPS_PORT)"
    echo "       --thrift-addr         : Specify listen address for Thrift API Gateway (default \$DEFAULT_THRIFT_ADDR)"
    echo "       --thrift-port         : Specify listen port for Thrift API Gateway (default \$DEFAULT_THRIFT_PORT)"
    echo "       --thrift-ssl-port     : Specify listen port for Thrift API SSL Gateway (default \$DEFAULT_THRIFT_SSL_PORT)"
    echo "       --grpc-addr           : Specify listen address for gRPC API Gateway (default \$DEFAULT_GRPC_ADDR)"
    echo "       --grpc-port           : Specify listen port for gRPC API Gateway (default \$DEFAULT_GRPC_PORT)"
    echo "       --ssl-keystore        : Specify SSL keystore file, relative file is prefixed with ./conf (default \$DEFAULT_SSL_KEYSTORE)"
    echo "       --ssl-keystorePassword: Specify listen port for gRPC API Gateway (default \$DEFAULT_SSL_KEYSTORE_PASSWORD)"
    echo
    echo "Example: start server 64mb memory limit, with custom configuration file"
    echo "    \${0##*/} start -m 64 -c abc.conf"
    echo
    exit 1
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

handleUnknownParam() {
    local PARAM=\$1
    shift
    local VALUE=\$1
    shift

    echo "ERROR: unknown parameter \"\$PARAM\""
    usageAndExit
}

parseParam() {
    # parse parameters: see https://gist.github.com/jehiah/855086
    local _number_='^[0-9]+\$'
    local PARAM=\$1
    shift
    local VALUE=\$1
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
            if ! [[ \$VALUE =~ \$_number_ ]]; then
                echo "ERROR: invalid memory value \"\$VALUE\""
                usageAndExit
            fi
            ;;

        -a|--addr)
            APP_ADDR=\$VALUE
            ;;

        -p|--port)
            APP_PORT=\$VALUE
            if ! [[ \$VALUE =~ \$_number_ ]]; then
                echo "ERROR: invalid HTTP port number \"\$VALUE\""
                usageAndExit
            fi
            ;;

        --https-port|--httpsPort)
            APP_HTTPS_PORT=\$VALUE
            if ! [[ \$VALUE =~ \$_number_ ]]; then
                echo "ERROR: invalid HTTPS port number \"\$VALUE\""
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
            if ! [[ \$VALUE =~ \$_number_ ]]; then
                echo "ERROR: invalid Thrift port number \"\$VALUE\""
                usageAndExit
            fi
            ;;

        --thrift-ssl-port|--thriftSslPort)
            APP_THRIFT_SSL_PORT=\$VALUE
            if ! [[ \$VALUE =~ \$_number_ ]]; then
                echo "ERROR: invalid Thrift SSL port number \"\$VALUE\""
                usageAndExit
            fi
            ;;

        --grpc-addr|--grpcAddr)
            APP_GRPC_ADDR=\$VALUE
            ;;

        --grpc-port|--grpcPort)
            APP_GRPC_PORT=\$VALUE
            if ! [[ \$VALUE =~ \$_number_ ]]; then
                echo "ERROR: invalid gRPC port number \"\$VALUE\""
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
            handleUnknownParam \$PARAM \$VALUE
            ;;
    esac
}

doAction() {
    local ACTION=\$1

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
}
