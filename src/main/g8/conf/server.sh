#!/bin/bash

# For Non-Production Env                                                       #
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
DEFAULT_APP_PORT=9090
DEFAULT_APP_MEM=64
DEFAULT_APP_CONF=application.conf
DEFAULT_APP_LOGBACK=logback-dev.xml
DEFAULT_APP_PID=\$APP_HOME/\$APP_NAME.pid
DEFAULT_APP_LOGDIR=\$APP_HOME/logs

APP_ADDR=\$DEFAULT_APP_ADDR
APP_PORT=\$DEFAULT_APP_PORT
APP_MEM=\$DEFAULT_APP_MEM
APP_CONF=\$DEFAULT_APP_CONF
APP_LOGBACK=\$DEFAULT_APP_LOGBACK
APP_PID=\$DEFAULT_APP_PID
APP_LOGDIR=\$DEFAULT_APP_LOGDIR

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
    RUN_CMD+=(-Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -J-server -J-Xms\${APP_MEM}m -J-Xmx\${APP_MEM}m)
    RUN_CMD+=(-Dspring.profiles.active=development -Dconfig.file=\$FINAL_APP_CONF -Dlogger.file=\$FINAL_APP_LOGBACK)
    RUN_CMD+=(\$JVM_EXTRA_OPS)
       
    "\${RUN_CMD[@]}" &
    disown \$!
    #echo \$! > "\$APP_PID"
    
    echo "STARTED \$APP_NAME `date`"
    
    echo "APP_ADDR     : \$APP_ADDR"
    echo "APP_PORT     : \$APP_PORT"
    echo "APP_MEM      : \$APP_MEM"
    echo "APP_CONF     : \$FINAL_APP_CONF"
    echo "APP_LOGBACK  : \$FINAL_APP_LOGBACK"
    echo "APP_LOGDIR   : \$APP_LOGDIR"
    echo "APP_PID      : \$APP_PID"
    echo "JVM_EXTRA_OPS: \$JVM_EXTRA_OPS"
}

usageAndExit() {
	echo "Usage: \${0##*/} <{start|stop|restart}> [-h] [--pid <.pid file>] [--logdir <log directory>] [-m <memory limit in mb>] [-a <http listen address>] [-p <http listen port>] [-c <custom config file>] [-l <custom logback config>] [-j \"<extra jvm options>\"]"
    echo "    stop   : stop the server"
    echo "    start  : start the server"
    echo "    restart: restart the server"
    echo "       -h or --help    : Display this help screen"
    echo "       -m or --mem     : JVM memory limit in mb (default \$DEFAULT_APP_MEM)"
    echo "       -a or --addr    : HTTP listen address (default \$DEFAULT_APP_ADDR)"
    echo "       -p or --port    : HTTP listen port (default \$DEFAULT_APP_PORT)"
    echo "       -c or --conf    : Custom app config file, relative file is prefixed with ./conf (default \$DEFAULT_APP_CONF)"
    echo "       -l or --logconf : Custom logback config file, relative file is prefixed with ./conf (default \$DEFAULT_APP_LOGBACK)"
    echo "       -j or --jvm     : Extra JVM options (example: \"-Djava.rmi.server.hostname=localhost)\""
    echo "       --pid           : Specify application's .pid file (default \$DEFAULT_APP_PID)"
    echo "       --logdir        : Specify application's log directory (default \$DEFAULT_APP_LOGDIR)"
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
                echo "ERROR: invalid port number \"\$APP_PORT\""
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

        -j|--jvm)
            JVM_EXTRA_OPS=\$VALUE
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
