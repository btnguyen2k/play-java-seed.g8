// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "$play_version$")

// Integrate with Eclipse: https://playframework.com/documentation/latest/IDE
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.2.4")

// Web plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.2") // https://github.com/sbt/sbt-coffeescript
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.2")         // https://github.com/sbt/sbt-less
addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.6")       // https://github.com/sbt/sbt-jshint
addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.10")         // https://github.com/sbt/sbt-rjs
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.3")       // https://github.com/sbt/sbt-digest
addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.2")        // https://github.com/sbt/sbt-mocha
addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.2")         // https://github.com/sbt/sbt-gzip
addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.11")      // https://github.com/irundaia/sbt-sassify
