# play-java-seed.g8

Giter8 template for generating a Play project in Java.

This project is the base template for my own Play projects in Java.

To create a project:

```
sbt new btnguyen2k/play-java-seed.g8
```

Latest release: [template-v0.1.5](RELEASE-NOTES.md).


## Features

- GitLab CI Runner sample (since [template-v0.1.3](RELEASE-NOTES.md))
- Intended for Linux-based applications
- Separated configurations for production and non-production environments:
  - For production: `conf/application-prod.conf` and `conf/logback-prod.xml`
  - For non-production: `conf/application.conf` and `conf/logback-dev.xml`
  - For cluster: `conf/application-cluster.conf`
- Start/Stop scripts (Linux shell scripts): `conf/server-prod.sh` for production, `conf/server.sh` for non-production
- Cluster support
- Samples:
  - Module
  - Form & Form controller
  - API controller
  - Workers & Scheduling
  - Cluster workers
- JVM tuning & GC logging

### Start/Stop Scripts

Commands:

- start: `sh conf/server.sh start` or `sh conf/server-prod.sh start`
- stop : `sh conf/server.sh stop` or `sh conf/server-prod.sh stop`

Command line arguments

- `-h` or `--help`: display help and exist
- `-a|--addr <listen-address>`: address to bind to, default `0.0.0.0`
- `-p|--port <http-port>`: port to bind to, default `9090`
- `-m|--mem <max-memory-in-mb>`: example `-m 64` will limit the JVM's memory to 64Mb
- `-c|--conf <config-file.conf>`: specify application's configuration file, default:
  - Production: `conf/application-prod.conf`
  - Non-production: `conf/application.conf`
- `-l|--logconf <logback-file.xml>`: logback configuration file, default:
  - Production: `conf/logback-prod.conf`
  - Non-production: `conf/logback-dev.conf`
- `-j|--jvm "extra-jvm-options"`: example `-j "-Djava.rmi.server.hostname=localhost)"`
- `--pid`: application's .pid file, default `${app.home}/${app.name}.pid`
- `--logdir`: application's log directory, default `${app.home}/logs`

Environment properties:

- `app.home`: point to application's home directory
- `app.logdir`: point to application's log directory
- `spring.profiles.active`: set to `production` for production environment and set to `development` otherwise

### Cluster mode

Dev env:

- `sh start_node1.sh`: start first node (http port `9001`, cluster TCP/IP port `9051`)
- `sh start_node2.sh`: start first node (http port `9002`, cluster TCP/IP port `9052`)
- `sh start_node3.sh`: start first node (http port `9003`, cluster TCP/IP port `9053`)


Production:

- start: `sh conf/server-cluster.sh start`
- stop : `sh conf/server-cluster.sh stop`

Command-line arguments for cluster mode:

- `--cluster-name <cluster-name>`: name of cluster (default: `MyCluster`). Nodes in one cluster must have same cluster-name value.
- `--cluster-addr <listen-address>`: listen address for cluster service (default: same value as application's http listen address).
- `--cluster-port <listen-port>`: listen port for cluster service (default: application's http port `+ 7`).
- `--cluster-seed <host:port>`: cluster seed node, must be in format `host:port`. Use multiple `--cluster-seed` to specify more than one seed nodes. If none specified, cluster mode is disabled

Example:

`./conf/server-cluster.sh start --cluster-name MyAwesomeCluster --cluster-port 9007 --cluster-addr 127.0.0.1 --cluster-seed 127.0.0.1:9007 --cluster-seed 127.0.0.1:9008`


## LICENSE & COPYRIGHT

This template is distributed under the same license as the original [`playframework/play-java-seed.g8`](https://github.com/playframework/play-java-seed.g8) template.

You are free to clone the repository, use, modify and redistribute the template.


## Giter8 template. 

For information on giter8 templates, please see http://www.foundweekends.org/giter8/

