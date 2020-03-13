
val tinkerGremlinV = "3.4.4"
val gremlinV = "3.4.4.5"
val csvParserV = "0.11.6"

lazy val graph = (project in file("."))
  .settings(
    name := "gsoc-2020",

    scalaVersion := "2.12.10",
    resolvers += Resolver.bintrayRepo("zamblauskas", "maven"),
    libraryDependencies ++= Seq(
      "org.apache.tinkerpop" % "tinkergraph-gremlin" % tinkerGremlinV withSources(),
      "com.michaelpollmeier" %% "gremlin-scala" % gremlinV withSources(),
      "zamblauskas" %% "scala-csv-parser" % csvParserV withSources()
    )
  )

