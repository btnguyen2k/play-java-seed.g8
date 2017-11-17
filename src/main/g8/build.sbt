import com.typesafe.config._

val conf       = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()
val appName    = conf.getString("app.name").toLowerCase().replaceAll("\\\\W+", "-")
val appVersion = conf.getString("app.version")

EclipseKeys.preTasks                 := Seq(compile in Compile)                     // Force compile project before running the eclipse command
EclipseKeys.skipParents in ThisBuild := false
EclipseKeys.projectFlavor            := EclipseProjectFlavor.Java                   // Java project. Don't expect Scala IDE
EclipseKeys.executionEnvironment     := Some(EclipseExecutionEnvironment.JavaSE18)  // expect Java 1.8
// Use .class files instead of generated .scala files for views and routes
EclipseKeys.createSrc                := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)

// Exclude the Play's the API documentation
sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

routesGenerator := InjectedRoutesGenerator

pipelineStages := Seq(digest, gzip)

// See https://playframework.com/documentation/2.6.x/AkkaHttpServer
lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayScala, PlayAkkaHttp2Support, SbtWeb).settings(
    name         := appName,
    version      := appVersion,
    organization := "$organization$"
)

scalaVersion := "$scala_version$"

// Custom Maven repository
resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/"

val _akkaClusterVersion      = "2.5.6"
val _playWsStandaloneVersion = "1.1.3"

val _grpcVersion             = "1.7.0"

val _springVersion           = "5.0.1.RELEASE"

val _ddthCacheAdapterVersion = "0.6.2"
val _ddthCommonsVersion      = "0.7.1.1"
val _ddthDaoVersion          = "0.8.3"

libraryDependencies ++= Seq(
    // we use Slf4j/Logback, so redirect Log4j to Slf4j
    "org.slf4j"                  % "log4j-over-slf4j"             % "1.7.25"
    
    ,"com.typesafe.akka"         %% "akka-cluster"                % _akkaClusterVersion
    ,"com.typesafe.akka"         %% "akka-distributed-data"       % _akkaClusterVersion
    ,"com.typesafe.akka"         %% "akka-cluster-metrics"        % _akkaClusterVersion
    ,"com.typesafe.akka"         %% "akka-cluster-tools"          % _akkaClusterVersion

    ,"com.typesafe.play"         %% "play-json"                   % "2.6.7"
    ,"com.typesafe.play"         %% "play-ahc-ws-standalone"      % _playWsStandaloneVersion
    ,"com.typesafe.play"         %% "play-ws-standalone-json"     % _playWsStandaloneVersion
    ,"com.typesafe.play"         %% "play-ws-standalone-xml"      % _playWsStandaloneVersion

    // RDMBS JDBC drivers & Connection Pool
    ,"org.hsqldb"                % "hsqldb"                       % "2.4.0"
    ,"mysql"                     % "mysql-connector-java"         % "6.0.6"
    ,"org.postgresql"            % "postgresql"                   % "42.1.4"
    ,"com.microsoft.sqlserver"   % "mssql-jdbc"                   % "6.2.2.jre8"
    ,"com.zaxxer"                % "HikariCP"                     % "2.7.3"

    ,"org.apache.thrift"         % "libthrift"                    % "0.10.0"

    ,"com.google.protobuf"       % "protobuf-java"                % "3.5.0"
    ,"io.grpc"                   % "grpc-core"                    % _grpcVersion
    ,"io.grpc"                   % "grpc-protobuf"                % _grpcVersion
    ,"io.grpc"                   % "grpc-stub"                    % _grpcVersion
    ,"io.grpc"                   % "grpc-netty"                   % _grpcVersion

    ,"org.springframework"       % "spring-beans"                 % _springVersion
    ,"org.springframework"       % "spring-expression"            % _springVersion
    ,"org.springframework"       % "spring-jdbc"                  % _springVersion

    // Commons
    ,"com.github.ddth"           % "ddth-commons-core"            % _ddthCommonsVersion
    ,"com.github.ddth"           % "ddth-commons-serialization"   % _ddthCommonsVersion

    // DAO
    ,"com.github.ddth"           % "ddth-dao-core"                % _ddthDaoVersion
    ,"com.github.ddth"           % "ddth-dao-jdbc"                % _ddthDaoVersion

    // Cache
    ,"com.github.ddth"           % "ddth-cache-adapter-core"      % _ddthCacheAdapterVersion
    ,"com.github.ddth"           % "ddth-cache-adapter-redis"     % _ddthCacheAdapterVersion
    ,"com.github.ddth"           % "ddth-cache-adapter-memcached" % _ddthCacheAdapterVersion

    ,filters
    ,javaWs
    ,guice
)
