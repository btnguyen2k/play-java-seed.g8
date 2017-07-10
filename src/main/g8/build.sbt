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

val _springVersion           = "4.3.9.RELEASE"
val _ddthCacheAdapterVersion = "0.6.1"
val _ddthCommonsVersion      = "0.6.3.2"
val _akkaVersion             = "2.5.3"
val _playWsStandaloneVersion = "1.0.1"

libraryDependencies ++= Seq(
    // we use Slf4j/Logback, so redirect Log4j to Slf4j
    "org.slf4j"                  % "log4j-over-slf4j"             % "1.7.25"
    
    ,"com.typesafe.akka"         %% "akka-cluster"                % _akkaVersion
    ,"com.typesafe.akka"         %% "akka-distributed-data"       % _akkaVersion
    ,"com.typesafe.akka"         %% "akka-cluster-metrics"        % _akkaVersion
    ,"com.typesafe.akka"         %% "akka-cluster-tools"          % _akkaVersion

    ,"com.typesafe.play"         %% "play-json"                   % "2.6.2"
    ,"com.typesafe.play"         %% "play-ahc-ws-standalone"      % _playWsStandaloneVersion
    ,"com.typesafe.play"         %% "play-ws-standalone-json"     % _playWsStandaloneVersion
    ,"com.typesafe.play"         %% "play-ws-standalone-xml"      % _playWsStandaloneVersion

    // MySQL is our default database
    ,"mysql"                     % "mysql-connector-java"         % "6.0.6"
    ,"com.zaxxer"                % "HikariCP"                     % "2.6.3"

    ,"org.apache.thrift"         % "libthrift"                    % "0.10.0"

    ,"org.springframework"       % "spring-beans"                 % _springVersion
    ,"org.springframework"       % "spring-expression"            % _springVersion
    ,"org.springframework"       % "spring-jdbc"                  % _springVersion

    ,"com.github.ddth"           % "ddth-commons-core"            % _ddthCommonsVersion
    ,"com.github.ddth"           % "ddth-commons-serialization"   % _ddthCommonsVersion
    ,"com.github.ddth"           % "ddth-dao-jdbc"                % "0.7.1"

    // Cache library
    ,"com.github.ddth"           % "ddth-cache-adapter-core"      % _ddthCacheAdapterVersion
    ,"com.github.ddth"           % "ddth-cache-adapter-redis"     % _ddthCacheAdapterVersion
    ,"com.github.ddth"           % "ddth-cache-adapter-memcached" % _ddthCacheAdapterVersion

    ,filters
    ,javaWs
    ,guice
)
