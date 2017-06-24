# $name$

$desc$ by $organization$.

Copyright (C) by $organization$.

Latest release version: `0.1.0`. See [RELEASE-NOTES.md](RELEASE-NOTES.md).

## Usage

**Start application**

```$ ./conf/server-prod.sh start
```

Command line arguments:

- `-m|--mem <memory limit in mb>`         : JVM memory limit in mb
- `-a|--addr <listen address>`            : HTTP listen address (default `0.0.0.0`)
- `-p|--port <listen port>                : HTTP listen port (default `9090`)
- `-c|--conf <config file .conf`          : Application's configuration file, relative file is prefixed with `./conf`
- `-l|--logconf <logback config file .xml>: Logback config file, relative file is prefixed with `./conf`
- `-j|--jvm <jvm options>                 : Extra JVM options (example: `-j "-Djava.rmi.server.hostname=localhost)"`)
- `--pid <application .pid file>          : Specify application's .pid file (default `${app.home}/${app.name}.pid`)
- `--logdir <application log directory>   : Specify application's log directory (default `${app.home}/logs`)

**Stop application**

```$ ./conf/server-prod.sh stop
```
