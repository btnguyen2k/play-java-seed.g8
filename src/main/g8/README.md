# $name$

$desc$ by $organization$.

Copyright (C) by $organization$.

Latest release version: `0.1.0`. See [RELEASE-NOTES.md](RELEASE-NOTES.md).

## Usage

**Start Standalone Application**

```shell
\$ ./conf/server-prod.sh start
```

Command line arguments:

- `-h|--help`                              : Display help  exit
- `--pid <path-to-.pid-file>`              : Path to application's .pid file (default `$name$.pid`)
- `-a|--addr <listen-address>`             : HTTP listen address (default `0.0.0.0`)
- `-p|--port <http-port>`                  : HTTP listen port (default `9000`) (value 0 will disable HTTP)
- `--https-port <https-port>`              : HTTPS listen port (default `9043`) (value 0 will disable HTTPS)
- `-m|--mem <max-memory-in-mb>`            : JVM memory limit in Mb (default `64` Mb)
- `-c|--conf <path-to-config-file.conf>`   : Application's configuration file, relative file is prefixed with `./conf` (default `application-prod.conf`)
- `-l|--logconf <path-to-logback-file.xml>`: Logback config file, relative file is prefixed with `./conf` (default `logback-prod.xml`)
- `--logdir <path-to-log-directory>`       : Directory to store log files
- `-j|--jvm "extra-jvm-options"`           : Extra JVM options (example: `-j "-Djava.rmi.server.hostname=localhost)"`, remember the double quotes!)
- `--thrift-addr <listen-address>`         : Listen address for Apache Thrift API gateway (default `0.0.0.0`)
- `--thrift-port <thrift-port>`            : Listen port for Apache Thrift API gateway (default `9005`) (value `0` will disable Thrift API gateway)
- `--thrift-ssl-port <thrift-ssl-port>`    : Listen port for Apache Thrift SSL API gateway (default `9048`) (value 0 will disable Thrift SSL API gateway)
- `--grpc-addr <listen-address>`           : Listen address for gRPC API gateway (default `0.0.0.0`)
- `--grpc-port <grpc-port>`                : Listen port for gRPC API gateway (default `9010`) (value `0` will disable gRPC API gateway)
- `--ssl-keystore <path-to-keystore-file>` : Path to keystore file (used by HTTPS & Thrift SSL)
- `--ssl-keystorePassword <password>`      : Keystore file's password

**Start Cluster Application**

```shell
\$ ./conf/server-cluster.sh start
```

Command line arguments: similar to standalone application, plus cluster-specified arguments:

- `-c|--conf <path-to-config-file.conf>`   : Application's configuration file, relative file is prefixed with `./conf` (default `application-cluster.conf`)
- `--cluster-name <cluster-name>`          : Cluster's logic name, used to separate nodes from one cluster to another (default `MyCluster`)
- `--cluster-addr <cluster-listen-address>`: Listen address for cluster protocol (default `127.0.0.1`). Note: use an interface's IP address (e.g. `192.168.1.2`), `0.0.0.0` is *not* a correct value!
- `--cluster-port <cluster-port>`          : Listen port for cluster protocol (default `9007`) (value 0 will start cluster node in non-master mode)
- `--cluster-seed <seed-node-host:port>`   : Cluster's seed node's host & port. Use multiple `--cluster-seed`s to specify more than one seed nodes. Must specify at least one seed.

**Stop Application**

```shell
\$ ./conf/server-prod.sh stop
```

or

```shell
\$ ./conf/server-cluster.sh stop
```

## For Developer

Required libraries/tools:

- sbt (https://www.scala-sbt.org/download.html)
- JDK 1.8+

Sbt will be used to compile, build, package, etc this project. The next section show  basic sbt command to build, package and generate project files for common IDEs.
See [sbt documentation](https://www.scala-sbt.org/documentation.html) and [Play! Framework documentation](https://www.playframework.com/documentation/latest/Build) for advanced topics.

**Generate Eclipse project**

```shell
\$ sbt eclipse
```

**Generate IntelliJ Project**

Simply open/import project with IntelliJ.

**Package Application as .zip file**

```shell
\$ sbt dist
```

The generated .zip file will be under directory `./target/universal/`.

**Build Docker image**

1- Build and Publish Docker image locally

```shell
\$ sbt docker:publishLocal
```

The command will build Docker image `$name$:$version$`.

2- Build Docker image manually (more control over the final Docker image)

Build project and generate necessary files to build Docker image (include `Dockerfile`)

```shell
\$  sbt docker:stage
```

The command will create necessary files under directory `./target/docker/`

The generated `Dockerfile` is ready-to-go but you are free to inspect and change it. Once you are happy, build Docker image normally, sample command:

```shell
\$ docker build --force-rm --squash -t $name$:$version$ ./target/docker/stage
```
