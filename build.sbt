sbtPlugin := true

lazy val root = (project in file(".")).enablePlugins(JavaAppPackaging, DockerPlugin).settings(
    name := "play-java-seed.g8",
    scriptedLaunchOpts ++= List("-Xms1024m", "-Xmx1024m", "-XX:ReservedCodeCacheSize=128m", "-XX:MaxPermSize=256m", "-Xss2m", "-Dfile.encoding=UTF-8"),
    resolvers += Resolver.url("typesafe", url("http://repo.typesafe.com/typesafe/ivy-releases/"))(Resolver.ivyStylePatterns)
)

//giter8.ScaffoldPlugin.scaffoldSettings
