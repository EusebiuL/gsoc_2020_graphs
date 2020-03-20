
val tinkerGremlinV = "3.4.4"
val gremlinV = "3.4.4.5"
val csvParserV = "0.11.6"

lazy val graph = (project in file("."))
  .settings(
    name := "gsoc-2020",

    scalaVersion := "2.12.8",
    scalacOptions in Compile ++= Seq(
      "-encoding", "utf8",
      "-Xfatal-warnings",
      "-deprecation",
      "-unchecked",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:postfixOps"
    ),
    mainClass in Compile := Some("com.gsoc.Main"),
    resolvers += Resolver.bintrayRepo("zamblauskas", "maven"),
    libraryDependencies ++= Seq(
      "com.michaelpollmeier" %% "gremlin-scala" % gremlinV withSources(),
      "org.apache.tinkerpop" % "gremlin-core" % "3.4.4" withSources(),
      "org.janusgraph" % "janusgraph-core" % "0.4.1" withSources(),
      "org.apache.tinkerpop" % "gremlin-driver" % "3.4.4" withSources(),
      "zamblauskas" %% "scala-csv-parser" % csvParserV withSources(),
      "org.janusgraph" % "janusgraph-hbase" % "0.4.1" withSources(),
      "org.apache.hbase" % "hbase-client" % "2.2.3" withSources(),
    ),

  )

