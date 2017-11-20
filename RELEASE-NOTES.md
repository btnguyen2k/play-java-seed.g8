# Release Notes


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
