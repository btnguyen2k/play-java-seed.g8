// App name & version
import com.typesafe.config._

val conf       = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()
val appName    = conf.getString("app.name").toLowerCase().replaceAll("\\W+", "-")
val appVersion = conf.getString("app.version")

// Custom Maven repository
resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/"

// See https://playframework.com/documentation/2.6.x/AkkaHttpServer
lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayScala, PlayAkkaHttp2Support, SbtWeb).settings(
    name         := appName,
    version      := appVersion,
    organization := "com.github.btnguyen2k",
    scriptedLaunchOpts ++= List("-Xms1024m", "-Xmx1024m", "-XX:ReservedCodeCacheSize=128m", "-XX:MaxPermSize=256m", "-Xss2m", "-Dfile.encoding=UTF-8"),
    resolvers += Resolver.url("typesafe", url("http://repo.typesafe.com/typesafe/ivy-releases/"))(Resolver.ivyStylePatterns)
)

// Eclipse configurations
EclipseKeys.preTasks                 := Seq(compile in Compile)                     // Force compile project before running the eclipse command
EclipseKeys.skipParents in ThisBuild := false
EclipseKeys.projectFlavor            := EclipseProjectFlavor.Java                   // Java project. Don't expect Scala IDE
EclipseKeys.executionEnvironment     := Some(EclipseExecutionEnvironment.JavaSE18)  // expect Java 1.8
// Use .class files instead of generated .scala files for views and routes
//EclipseKeys.createSrc                := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)

// Exclude the Play's the API documentation
sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
routesGenerator := InjectedRoutesGenerator
pipelineStages := Seq(digest, gzip)

// Dependency configurations
val _akkaClusterVersion      = "2.5.8"
val _playWsStandaloneVersion = "1.1.3"
val _grpcVersion             = "1.8.0"
val _springVersion           = "5.0.2.RELEASE"
val _ddthCommonsVersion      = "0.7.1.1"
val _ddthCacheAdapterVersion = "0.6.3.2"
val _ddthDaoVersion          = "0.8.4"
val _ddthDLockVersion        = "0.1.0"

libraryDependencies ++= Seq(
    // we use Slf4j/Logback, so redirect Log4j to Slf4j
    "org.slf4j"                  % "log4j-over-slf4j"             % "1.7.25"

    ,"com.typesafe.akka"         %% "akka-cluster"                % _akkaClusterVersion
    ,"com.typesafe.akka"         %% "akka-distributed-data"       % _akkaClusterVersion
    ,"com.typesafe.akka"         %% "akka-cluster-metrics"        % _akkaClusterVersion
    ,"com.typesafe.akka"         %% "akka-cluster-tools"          % _akkaClusterVersion

    ,"com.typesafe.play"         %% "play-json"                   % "2.6.8"
    ,"com.typesafe.play"         %% "play-ahc-ws-standalone"      % _playWsStandaloneVersion
    ,"com.typesafe.play"         %% "play-ws-standalone-json"     % _playWsStandaloneVersion
    ,"com.typesafe.play"         %% "play-ws-standalone-xml"      % _playWsStandaloneVersion

    // RDMBS JDBC drivers & Connection Pool
    ,"org.hsqldb"                % "hsqldb"                       % "2.4.0"
    ,"mysql"                     % "mysql-connector-java"         % "6.0.6"
    ,"org.postgresql"            % "postgresql"                   % "42.1.4"
    ,"com.microsoft.sqlserver"   % "mssql-jdbc"                   % "6.2.2.jre8"
    ,"com.zaxxer"                % "HikariCP"                     % "2.7.4"

    ,"org.apache.thrift"         % "libthrift"                    % "0.10.0"

    ,"com.google.protobuf"       % "protobuf-java"                % "3.5.1"
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

    // DLock
    ,"com.github.ddth"           % "ddth-dlock-core"              % _ddthDLockVersion
    ,"com.github.ddth"           % "ddth-dlock-redis"             % _ddthDLockVersion

    ,filters
    ,javaWs
    ,guice

    ,"org.webjars"               % "AdminLTE"                     % "2.4.2"
)
