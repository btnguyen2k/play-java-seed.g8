# play-java-seed.g8

Giter8 template for generating a Play project in Java.

This template is cloned from [playframework/play-java-seed.g8](https://github.com/playframework/play-java-seed.g8)
and is for my personal Play projects.

However, you are welcomed to use it. Feedback and comments are appreciated!

To create a project:

```shell
sbt new btnguyen2k/play-java-seed.g8
```

Scaffolding:
- [RESTFul/Thrift/gRPC APIs](src/main/scaffolds/sampleApis/README.md).
- [Scheduled jobs](src/main/scaffolds/sampleWorkers/README.md).
- [GUI: a ControlPanel using Bootstrap](src/main/scaffolds/sampleControlPanel/README.md).

Latest release: [template-v2.7.r1](RELEASE-NOTES.md).

## Features

- Play! Framework v2.7.x:
  - [What’s new in Play 2.7](https://www.playframework.com/documentation/2.7.x/Highlights27)
  - [Play 2.7 Migration Guide](https://www.playframework.com/documentation/2.7.x/Migration27)
- Scheduled jobs with `ddth-akka` (since [template-v2.6.r8](RELEASE-NOTES.md)).
- API service (JSON-based data format) using `ddth-recipes` (since [template-v2.6.r8](RELEASE-NOTES.md)); supported interfaces:
  - HTTP
  - [Apache Thrift](https://thrift.apache.org).
  - [gRPC](https://grpc.io).
- Swagger support for REST APIs (since [template-v2.7.r1](RELEASE-NOTES.md)):
  - Swagger Json file & API annotations.
  - Swagger UI.
  - See examples at [sampleApis/conf/samplesApi.routes](src/main/scaffolds/sampleApis/conf/samplesApi.routes).
- Docker support (since [template-v2.6.r6](RELEASE-NOTES.md)).
- HTTPS & HTTP/2 support (since [template-v2.6.r1](RELEASE-NOTES.md)).
- GitLab CI Runner sample (since [template-v0.1.3](RELEASE-NOTES.md)).
- Cluster support.
- Separated configurations for production and non-production environments:
  - For production: `conf/application-prod.conf` and `conf/logback-prod.xml`
  - For non-production: `conf/application.conf` and `conf/logback-dev.xml`
  - For cluster: `conf/application-cluster.conf`
- Start/Stop scripts for Linux:
  - For production: `conf/server-prod.sh`
  - For non-production: `conf/server.sh`
- Scaffolding & Samples:
  - [RESTFul/Thrift/gRPC APIs](src/main/scaffolds/sampleApis/README.md).
  - [Scheduled jobs](src/main/scaffolds/sampleWorkers/README.md).
  - [GUI: a ControlPanel using Bootstrap](src/main/scaffolds/sampleControlPanel/README.md).
  - Modules
  - I18N
- JVM tuning & GC logging
- Custom error pages (since [template-v2.6.r9](RELEASE-NOTES.md)).

### Swagger Specs Generator for REST APIs

Since [template-v2.7.r1](RELEASE-NOTES.md), this template use [com.iheart:play-swagger](https://github.com/iheartradio/play-swagger) to generate Swagger specs file in Json format.
The specs file can be accessed via `http(s)://host:port/api-swagger.json`. The URI is configured in `routes` file and can be changed.

[Swagger UI](https://swagger.io/tools/swagger-ui/) is also included and can be accessed via `http(s)://host:port/api-docs/`.

To write API documentation in Swagger spec:
- Write the document in `routes` file. Currently this template support [Swagger v2.0 syntax](https://swagger.io/docs/specification/2-0/basic-structure/).
- Examples at [sampleApis/conf/samplesApi.routes](src/main/scaffolds/sampleApis/conf/samplesApi.routes).
- See [com.iheart:play-swagger GitHub page](https://github.com/iheartradio/play-swagger) for more information.

Remember to set the `routes` file to be parsed for API specs in `application.conf`:

```
swagger.routes.file = samplesApi.routes
```

### Start/Stop Scripts

Commands:

- Start: `sh conf/server.sh start` (non-production) or `sh conf/server-prod.sh start` (production)
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
A copy of its licence can be found in [LICENSE.md](LICENSE.md).

You are free to clone the repository and use, modify and redistribute the template.


## Giter8 template. 

For information on giter8 templates, please see http://www.foundweekends.org/giter8/
