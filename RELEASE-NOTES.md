# Release Notes

## 2019-08-23: template-v2.7.r1

**Highlighted changes:**

- Migrate to `Java 11`
- Upgrade to `Play! Framework v2.7.3`:
  - [What’s new in Play 2.7](https://www.playframework.com/documentation/2.7.x/Highlights27)
  - [Play 2.7 Migration Guide](https://www.playframework.com/documentation/2.7.x/Migration27)
- Add Swagger support.
- Use `G1GC` as default GC (can be changed via start script).
- Aggressive small memory footprint (`-XX:MinHeapFreeRatio=5 -XX:MaxHeapFreeRatio=10 -XX:-ShrinkHeapInSteps`)


**Breaking changes**
- Template file now needs an `implicit request: Http.Request` parameter.
It will be passed to template via `BasePageController.render(...)`.
See [Play Documentation - Java Http.Context changes](https://www.playframework.com/documentation/2.7.x/JavaHttpContextMigration27#Some-template-tags-need-an-implicit-Request,-Messages-or-Lang-instance) for more information.
- Methods of `BaseController` that read/write cookies or session now require either `Request`, `Result` or `Cookies` as a parameter.

**Upgrade to Play! Framework v2.7.3:**

- Play! Framework   : v2.7.3
- Scala             : v2.12.9
- Sbt               : v1.2.8
- Akka              : v2.5.23
- play-ws-standalone: v2.0.7
- play-json         : v2.7.4

RDMBS JDBC drivers & Connection Pool:
- HikariCP: v3.3.1
- HSQLDB  : v2.5.0

Spring Framework: v5.1.9.RELEASE

RPC:
- libthrift: v0.12.0
- protobuf : v3.9.1
- gRPC     : v1.22.1
- netty-tcnative-boringssl-static: v2.0.25.Final

DDTH:
- ddth-akka         : v1.1.0.1
- ddth-cache-adapter: v1.0.0
- ddth-commons      : v1.1.0
- ddth-dao          : v1.0.0
- ddth-dlock        : v1.0.0
- ddth-queue        : v1.0.0
- ddth-recipes      : v1.0.0

Swagger:
- com.iheart:play-swagger: v0.8.0-PLAY2.7
- Webjars swagger-ui     : v3.23.5

Others:
- Google's Guava        : v28.0-jre
- Apache's commons-pool2: v2.7.0
- Webjars AdminLTE      : v2.4.15
  - Webjars Bootstrap        : v3.4.1
  - Webjars Font-Awesome     : v4.7.0
  - Webjars jQuery           : v3.4.1
  - Webjars jQuery-slimScroll: v1.3.8
  - Webjars Ionicons         : v2.0.1

Plugins:
- com.typesafe.sbteclipse:sbteclipse-plugin:5.2.4
- com.typesafe.sbt:sbt-native-packager:1.3.25
- com.typesafe.sbt:sbt-rjs:1.0.10
- com.typesafe.sbt:sbt-digest:1.1.4
- com.typesafe.sbt:sbt-gzip:1.0.2
- Gitter8 Scaffold: `org.foundweekends.giter8:sbt-giter8-scaffold:0.12.0-M2`


## 2018-10-03: template-v2.6.r9

- Upgrade to Play! Framework v2.6.19
- Add custom error pages for 400, 403, 404, 500
- Other libs:
   - `ddth-dao:0.9.0.5`
   

## 2018-09-12: template-v2.6.r8

**Highlighted changes**

- `Play! Framework v2.6.18`
- New configuration `bootstrap-actors`: a list of application actors to be created when application starts
  - Format: a list of `fully-qualified-class-name[;actor-name]`
- Schedule jobs using `ddth-akka`; top level package `akka` is no longer needed, hence it is removed.
- Implement API service using `ddth-recipes`; top level package `api` is no longer needed, hence it is removed.

**Upgrade to Play! Framework v2.6.18:**

- Play! Framework   : v2.6.18
- Scala             : v2.12.6
- Akka              : v2.5.16
- play-json         : v2.6.10
- play-ws-standalone: v1.1.10

RDMBS JDBC drivers & Connection Pool:
- HikariCP  : v3.2.0
- HSQLDB    : v2.4.1
- MySQL     : v8.0.12
- PostgreSQL: v42.2.5
- MSSQL     : v7.0.0.jre8

Spring Framework: v5.0.8.RELEASE

RPC:
- libthrift: v0.11.0
- protobuf : v3.6.1
- gRPC     : v1.14.0
- netty-tcnative-boringssl-static: v2.0.15.Final

DDTH:
- ddth-recipes      : v0.2.0.1
- ddth-commons      : v0.9.1.7
- ddth-cache-adapter: v0.6.3.3
- ddth-dao          : v0.9.0.4
- ddth-akka         : v0.1.4
- ddth-dlock        : v0.1.2
- ddth-queue        : v0.7.1.2

Others:
- Guava        : v20.0
- commons-pool2: v2.6.0

Plugins:
- com.typesafe.sbteclipse:sbteclipse-plugin:5.2.4
- com.typesafe.sbt:sbt-native-packager:1.3.6
- com.typesafe.sbt:sbt-coffeescript:1.0.2
- com.typesafe.sbt:sbt-less:1.1.2
- com.typesafe.sbt:sbt-jshint:1.0.6
- com.typesafe.sbt:sbt-rjs:1.0.10
- com.typesafe.sbt:sbt-digest:1.1.4
- com.typesafe.sbt:sbt-mocha:1.1.2
- com.typesafe.sbt:sbt-gzip:1.0.2
- org.irundaia.sbt:sbt-sassify:1.4.12
- Gitter8: `org.foundweekends.giter8:sbt-giter8:0.11.0`
- Gitter8 Scaffold: `org.foundweekends.giter8:sbt-giter8-scaffold:0.11.0`


## 2018-03-02: template-v2.6.r7

- Bug fixes & enhancements.


## 2018-02-28: template-v2.6.r6

**g8Scaffold**

- Refactor & move samples to scaffolds: `sampleApis`, `sampleControlPanel`, `sampleWorkers`.
- Script `clear_scaffold.sh` to clean-up scaffolds.
- Docker support.


## 2018-01-09: template-v2.6.r5

**Distributed worker**

- `BaseWorker`: use a local in-memory lock, so that `BaseWorker` remains local-only worker.
- New class `BaseDistributedWorker`: look up `IDLockFactory` from `IRegistry.getBean(IDLockFactory.class)`.
  - If the looked-up `IDLockFactory` is a non-distributed-implementation (e.g. `InmemDLock`), `BaseDistributedWorker` behaves just like `BaseWorker`.
  - If the looked-up `IDLockFactory` is a distributed-implementation (e.g. `RedisDLockFactory`), `BaseDistributedWorker` behaves simular to `BaseClusterWorker`.


**Sample Admin Control Panel**

- Template: AdminLTE2 admin template.
- I18N samples: English & Vietnamese.
- Forms & Pages:
  - Login & Logout
  - Usergroup list
  - Create, Edit, Delete usergroups
  - User list
  - Create, Edit, Delete users


## 2018-01-02: template-v2.6.r4

**Support multiple Spring's configuration files**

- (Still) Defined via configuration key `spring.conf`
- Multiple configuration files are separated by `[,;\s]+`

**Refactor, Bug fixes and Enhancements**

- Move samples' route configurations to file `conf/samples.routes`
- `default.properties`: add `*.java` to verbatim list
- `BaseWorker`: use `ddth-dlock` instead of inner process lock, so that `BaseWorker` can be a drop-in replacement for `BaseClusterWorker`
- Other bug fixes and enhancements

**Upgrade to Play! Framework v2.6.10:**

- Play! Framework: v2.6.10
- Scala: v2.12.4
- Akka: v2.5.8
- play-json: v2.6.8
- play-ws-standalone: v1.1.3

SBT: v1.0.4

RDMBS JDBC drivers & Connection Pool:
- HSQLDB: v2.4.0
- MySQL: v6.0.6
- PostgreSQL: v42.1.4
- MSSQL: v6.2.2.jre8
- HikariCP: v2.7.4

RPC:
- libthrift: v0.10.0
- protobuf: v3.5.1
- gRPC: v1.8.0

Spring Framework: v5.0.2.RELEASE

DDTH:
- ddth-commons: v0.7.1.1
- ddth-cache: v0.6.3.2
- ddth-dao: v0.8.4
- ddth-dlock: v0.1.0

Plugins:
- com.typesafe.sbteclipse:sbteclipse-plugin:5.2.4
- com.typesafe.sbt:sbt-coffeescript:1.0.2
- com.typesafe.sbt:sbt-less:1.1.2
- com.typesafe.sbt:sbt-jshint:1.0.6
- com.typesafe.sbt:sbt-rjs:1.0.10:
- com.typesafe.sbt:sbt-digest:1.1.3
- com.typesafe.sbt:sbt-mocha:1.1.2
- com.typesafe.sbt:sbt-gzip:1.0.2
- org.irundaia.sbt:sbt-sassify:1.4.11

Disable plugin "org.foundweekends.giter8:sbt-giter8-scaffold:0.x" as not working yet with sbt 1.x


## 2017-11-20: template-v2.6.r3

Upgrade to Play! Framework v2.6.7:

- Play! Framework: v2.6.7
- Scala: v2.12.4
- Akka: v2.5.6
- play-json: v2.6.7
- play-ws-standalone: v1.1.3
- sbt: v1.0.3

Plugins:
- com.typesafe.sbteclipse:sbteclipse-plugin:5.2.3
- com.typesafe.sbt:sbt-coffeescript:1.0.2
- com.typesafe.sbt:sbt-less:1.1.2
- com.typesafe.sbt:sbt-jshint:1.0.6
- com.typesafe.sbt:sbt-rjs:1.0.10:
- com.typesafe.sbt:sbt-digest:1.1.3
- com.typesafe.sbt:sbt-mocha:1.1.2
- com.typesafe.sbt:sbt-gzip:1.0.2
- org.irundaia.sbt:sbt-sassify:1.4.11

Disable plugin "org.foundweekends.giter8:sbt-giter8-scaffold" as not working yet with sbt 1.x.

RDMBS JDBC drivers & Connection Pool:
- MySQL: v6.0.6
- PostgreSQL: v42.1.4
- MSSQL: v6.2.2.jre8
- HikariCP: v2.7.3

RPC:
- libthrift: v0.10.0
- protobuf: v3.5.0
- gRPC: v1.7.0

Spring Framework: v5.0.1.RELEASE

DDTH:
- ddth-commons: v0.7.0.1
- ddth-dao: v0.8.3
- ddth-cache: v0.6.2

Restructure samples (REST APIs, Form page & Views).

Other fixes and enhancements.


## 2017-07-12: template-v2.6.r2

- Support [gRPC](https://grpc.io) API Interface.
- Revised [Apache Thrift](https://thrift.apache.org) API: Apache Thrift & gRPC should have same API interface.
- Bug fixes & enhancements.


## 2017-07-10: template-v2.6.r1

First release of Play! 2.6.x template.

- HEAD branch is now `2.6.x`, upgrade to `Play! 2.6.1`, Scala `2.12.2` and SBT `0.13.15`
- Bump version number to match with Play! version: template `2.6.r<release>` for Play! version `2.6.x`.
- HTTPS & HTTP/2 support.
- Pre-define thread pools for different use-cases.
- Use only one actor system in cluster mode.
- More cluster-worker samples.
