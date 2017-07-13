# $name$

$desc$ by $organization$.

Copyright (C) by $organization$.

Latest release version: `0.1.0`. See [RELEASE-NOTES.md](RELEASE-NOTES.md).

## Usage

**Start Standalone Application**

```\$ ./conf/server-prod.sh start
```

Command line arguments:

- `-h|--help`                              : Display help & exit
- `--pid <path-to-.pid-file>`              : Path to application's .pid file (default `$name$.pid`)
- `-a|--addr <listen-address>`             : HTTP listen address (default `0.0.0.0`)
- `-p|--port <http-port>`                  : HTTP listen port (default `9000`) (value 0 will disable HTTP)
- `--https-port <https-port>`              : HTTPS listen port (default `9022`) (value 0 will disable HTTPS)
- `-m|--mem <max-memory-in-mb>`            : JVM memory limit in Mb (default `64` Mb)
- `-c|--conf <path-to-config-file.conf>`   : Application's configuration file, relative file is prefixed with `./conf` (default `application-prod.conf`)
- `-l|--logconf <path-to-logback-file.xml>`: Logback config file, relative file is prefixed with `./conf` (default `logback-prod.xml`)
- `--logdir <path-to-log-directory>`       : Directory to store log files
- `-j|--jvm "extra-jvm-options"`           : Extra JVM options (example: `-j "-Djava.rmi.server.hostname=localhost)"`, remember the double quotes!)
- `--thrift-addr <listen-address>`         : Listen address for Apache Thrift API gateway (default `0.0.0.0`)
- `--thrift-port <thrift-port>`            : Listen port for Apache Thrift API gateway (default `9005`) (value 0 will disable Thrift API gateway)
- `--thrift-ssl-port <thrift-ssl-port>`    : Listen port for Apache Thrift SSL API gateway (default `9027`) (value 0 will disable Thrift SSL API gateway)
- `--grpc-addr <listen-address>`           : Listen address for gRPC API gateway (default `0.0.0.0`)
- `--grpc-port <grpc-port>`                : Listen port for gRPC API gateway (default `9010`) (value 0 will disable gRPC API gateway)
- `--ssl-keystore <path-to-keystore-file>` : Path to keystore file (used by HTTPS & Thrift SSL)
- `--ssl-keystorePassword <password>`      : Keystore file's password

**Start Cluster Application**

```\$ ./conf/server-cluster.sh start
```

Command line arguments: similar to standalone application, plus cluster-dedicated ones

- `-c|--conf <path-to-config-file.conf>`   : Application's configuration file, relative file is prefixed with `./conf` (default `application-cluster.conf`)
- `--cluster-name <cluster-name>`          : Cluster's logic name, used to separate nodes from one cluster to another (default `MyCluster`)
- `--cluster-addr <cluster-listen-address>`: Listen address for cluster protocol (default `127.0.0.1`). Note: use an interface's IP address (e.g. `192.168.1.2`), `0.0.0.0` is not a correct value!
- `--cluster-port <cluster-port>`          : Listen port for cluster protocol (default `9007`) (value 0 will start cluster node in non-master mode)
- `--cluster-seed <seed-node-host:port>`   : Cluster's seed node's host & port. Use multiple `--cluster-seed`s to specify more than one seed nodes. Must specify at least one seed.

**Stop Application**

```\$ ./conf/server-prod.sh stop
```

or

```\$ ./conf/server-cluster.sh stop
```
