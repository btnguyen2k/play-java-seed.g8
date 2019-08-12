// App name & version
import com.typesafe.config._
val conf       = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()
val appName    = conf.getString("app.name").toLowerCase().replaceAll("\\\\W+", "-")
val appVersion = conf.getString("app.version")

sbtPlugin    := true
scalaVersion := "$scala_version$"
//giter8.ScaffoldPlugin.projectSettings

// Custom Maven repository
resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/"

// See https://playframework.com/documentation/latest/AkkaHttpServer
lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayScala, PlayAkkaHttpServer, PlayAkkaHttp2Support, SbtWeb).disablePlugins(ScriptedPlugin).settings(
    name         := appName,
    version      := appVersion,
    organization := "$organization$",
    //scriptedLaunchOpts ++= List("-Xms1024m", "-Xmx1024m", "-XX:ReservedCodeCacheSize=128m", "-XX:MaxPermSize=256m", "-Xss2m", "-Dfile.encoding=UTF-8"),
    resolvers += Resolver.url("typesafe", url("http://repo.typesafe.com/typesafe/ivy-releases/"))(Resolver.ivyStylePatterns)
)

// Eclipse configurations
EclipseKeys.preTasks                 := Seq(compile in Compile, compile in Test)    // Force compile project before running the eclipse command
EclipseKeys.skipParents in ThisBuild := false
EclipseKeys.projectFlavor            := EclipseProjectFlavor.Java                   // Java project. Don't expect Scala IDE
//EclipseKeys.executionEnvironment     := Some(EclipseExecutionEnvironment.JavaSE18)  // No Java11 yet, manually switch to JDK11 in Eclipse!
// Use .class files instead of generated .scala files for views and routes
//EclipseKeys.createSrc                := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)

/* Docker packaging options */
// Manual docker build:
//// 1. dir\$ sbt clean docker:stage
//// 2. dir\$ docker build --force-rm --squash -t $name$:$version$ ./target/docker/stage
// Auto docker build (local):
//// 1. dir\$ sbt clean docker:publishLocal
dockerCommands := Seq()
import com.typesafe.sbt.packager.docker._
dockerCommands := Seq(
    Cmd("FROM"          , "openjdk:11-jre-alpine"),
    Cmd("LABEL"         , "maintainer=\"$author$\""),
    Cmd("ADD"           , "opt /opt"),
    Cmd("RUN"           , "apk add --no-cache -U bash ca-certificates tzdata && ln -s /opt/docker /opt/" + appName + " && chown -R daemon:daemon /opt && chmod 755 /opt/docker/conf/*.sh && chmod 755 /opt/docker/bin/*"),
    Cmd("RUN"           , "cp /usr/share/zoneinfo/$timezone$ /etc/localtime"),
    Cmd("WORKDIR"       , "/opt/" + appName),
    Cmd("USER"          , "daemon"),
    ExecCmd("ENTRYPOINT", "./conf/server-docker.sh", "start")
)
packageName in Docker := appName
version in Docker     := appVersion

// Exclude the Play's the API documentation
sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false

javacOptions    ++= Seq("-source", "11", "-target", "11")
routesGenerator := InjectedRoutesGenerator
pipelineStages  := Seq(rjs, digest, gzip)

// Dependency configurations
val _akkaVersion             = "2.5.23"
val _playWsStandaloneVersion = "2.0.7"
val _grpcVersion             = "1.22.1"
val _springVersion           = "5.1.9.RELEASE"
val _ddthAkkaVersion         = "1.0.0"
val _ddthCacheAdapterVersion = "1.0.0"
val _ddthCommonsVersion      = "1.1.0"
val _ddthDaoVersion          = "1.0.0"
val _ddthDLockVersion        = "1.0.0"
val _ddthQueueVersion        = "1.0.0"

libraryDependencies ++= Seq(
    // we use Slf4j/Logback, so redirect Log4j to Slf4j
    "org.slf4j"                  % "log4j-over-slf4j"             % "1.7.27"

    // Akka actor
    ,"com.typesafe.akka"         %% "akka-actor"                  % _akkaVersion
    ,"com.typesafe.akka"         %% "akka-cluster"                % _akkaVersion
    ,"com.typesafe.akka"         %% "akka-distributed-data"       % _akkaVersion
    ,"com.typesafe.akka"         %% "akka-cluster-metrics"        % _akkaVersion
    ,"com.typesafe.akka"         %% "akka-cluster-tools"          % _akkaVersion

    // Play JSON & WebServices
    ,"com.typesafe.play"         %% "play-json"                   % "2.7.4"
    ,"com.typesafe.play"         %% "play-ahc-ws-standalone"      % _playWsStandaloneVersion
    ,"com.typesafe.play"         %% "play-ws-standalone-json"     % _playWsStandaloneVersion
    ,"com.typesafe.play"         %% "play-ws-standalone-xml"      % _playWsStandaloneVersion

    // RDMBS JDBC drivers & Connection Pool
    ,"com.zaxxer"                % "HikariCP"                     % "3.3.1"

    //// comment out or remove unused JDBC drivers
    ,"org.hsqldb"                % "hsqldb"                       % "2.5.0"
    //,"mysql"                     % "mysql-connector-java"         % "8.0.17"
    //,"org.postgresql"            % "postgresql"                   % "42.2.6"
    //,"com.microsoft.sqlserver"   % "mssql-jdbc"                   % "7.4.1.jre11"

    ,"com.google.guava"          % "guava"                        % "28.0-jre"
    ,"org.apache.commons"        % "commons-pool2"                % "2.7.0"
    ,"com.github.ddth"           % "ddth-recipes"                 % "1.0.0"

    // RPC: Thrift
    ,"org.apache.thrift"         % "libthrift"                    % "0.12.0"

    // RPC: gRPC
    ,"com.google.protobuf"       % "protobuf-java"                % "3.9.1"
    ,"io.grpc"                   % "grpc-netty"                   % _grpcVersion
    ,"io.grpc"                   % "grpc-protobuf"                % _grpcVersion
    ,"io.grpc"                   % "grpc-stub"                    % _grpcVersion
    ,"io.grpc"                   % "grpc-core"                    % _grpcVersion
    ,"io.netty"                  % "netty-tcnative-boringssl-static" %  "2.0.25.Final"

    // Spring Framework
    ,"org.springframework"       % "spring-beans"                 % _springVersion
    ,"org.springframework"       % "spring-expression"            % _springVersion
    ,"org.springframework"       % "spring-jdbc"                  % _springVersion

    // DDTH-Commons: https://github.com/DDTH/ddth-commons
    ,"com.github.ddth"           % "ddth-commons-core"            % _ddthCommonsVersion
    ,"com.github.ddth"           % "ddth-commons-serialization"   % _ddthCommonsVersion

    // DDTH-DAO: https://github.com/DDTH/ddth-dao
    ,"com.github.ddth"           % "ddth-dao-core"                % _ddthDaoVersion
    ,"com.github.ddth"           % "ddth-dao-jdbc"                % _ddthDaoVersion

    // DDTH-Cache: https://github.com/DDTH/ddth-cache-adapter
    ,"com.github.ddth"           % "ddth-cache-adapter-core"      % _ddthCacheAdapterVersion
    ,"com.github.ddth"           % "ddth-cache-adapter-redis"     % _ddthCacheAdapterVersion

    // DDTH-Akka: https://github.com/DDTH/ddth-akka
    ,"com.github.ddth"           % "ddth-akka-core"               % _ddthAkkaVersion

    // DDTH-DLock: https://github.com/DDTH/ddth-dlock
    ,"com.github.ddth"           % "ddth-dlock-core"              % _ddthDLockVersion
    ,"com.github.ddth"           % "ddth-dlock-redis"             % _ddthDLockVersion

    // DDTH-Queue: https://github.com/DDTH/ddth-queue
    ,"com.github.ddth"           % "ddth-queue-core"              % _ddthQueueVersion
    ,"com.github.ddth"           % "ddth-queue-redis"             % _ddthQueueVersion

    ,filters
    ,javaWs
    ,guice

    ,"org.webjars"               % "AdminLTE"                     % "2.4.15"
)
