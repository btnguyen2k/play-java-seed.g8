# play-java-seed.g8

Giter8 template for generating a Play project in Java.

This template is for my personal Play projects, but you are free to use it. Feedback and comments are always welcomed!

To create a project:

```shell
sbt new btnguyen2k/play-java-seed.g8
```

Latest release: [template-v2.6.r8](RELEASE-NOTES.md).

## Features

- Docker support (since [template-v2.6.r6](RELEASE-NOTES.md)).
- HTTPS & HTTP/2 support (since [template-v2.6.r1](RELEASE-NOTES.md)).
- GitLab CI Runner sample (since [template-v0.1.3](RELEASE-NOTES.md)).
- Start/Stop scripts for Linux
- Separated configurations for production and non-production environments:
  - For production: `conf/application-prod.conf` and `conf/logback-prod.xml`
  - For non-production: `conf/application.conf` and `conf/logback-dev.xml`
  - For cluster: `conf/application-cluster.conf`
- Start/Stop scripts (Linux shell scripts): `conf/server-prod.sh` for production, `conf/server.sh` for non-production
- Cluster support
- Samples:
  - Module
  - Sample Admin Control Panel using AdminLTE2 Template: Form & Page controllers (since [template-v2.6.r5](RELEASE-NOTES.md)).
  - I18N
  - Workers & Scheduling
  - Cluster workers
  - APIs: JSON-based data format. Supported interfaces:
    - HTTP(S)
    - Apache Thrift (since [template-v1.4.0](RELEASE-NOTES.md)).
    - gRPC (since [template-v2.6.r2](RELEASE-NOTES.md)).
- JVM tuning & GC logging

### Start/Stop Scripts

Commands:

- Start: `sh conf/server.sh start` or `sh conf/server-prod.sh start`
- Stop : `sh conf/server.sh stop` or `sh conf/server-prod.sh stop`

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
- `--cluster-addr <listen-address>`: listen address for cluster service (default: `127.0.0.1`).
- `--cluster-port <listen-port>`: listen port for cluster service (default: `9007`, value 0 will start cluster node in non-master mode).
- `--cluster-seed <host:port>`: cluster seed node, must be in format `host:port`. Use multiple `--cluster-seed` to specify more than one seed nodes. Must specify at least one seed.

Example:

`./conf/server-cluster.sh start --cluster-name MyAwesomeCluster --cluster-port 9007 --cluster-addr 127.0.0.1 --cluster-seed 127.0.0.1:9007 --cluster-seed 127.0.0.1:9008`

### Docker support

Since `template-v2.6.r6`, application can be packaged into a Docker image.

1- Build and Publish Docker image locally

```shell
sbt docker:publishLocal
```

The command will build Docker image `$name$:$version$`.

2- Build Docker image manually (more control over the final Docker image)

Build project and generate necessary files to build Docker image (include `Dockerfile`)

```shell
sbt docker:stage
```

The command will create necessary files under directory `./target/docker/`

The generated `Dockerfile` is ready-to-go but you are free to inspect and change it. Once you are happy, build Docker image normally, sample command:

```shell
docker build --force-rm --squash -t $name$:$version$ ./target/docker/stage
```


## LICENSE & COPYRIGHT

This template is distributed under the same license as the original [`playframework/play-java-seed.g8`](https://github.com/playframework/play-java-seed.g8) template.

You are free to clone the repository, use, modify and redistribute the template.


## Giter8 template. 

For information on giter8 templates, please see http://www.foundweekends.org/giter8/

