
val gremlinV = "3.4.4.5"

lazy val graph = (project in file("."))
  .settings(
    name := "gsoc-2020",

    scalaVersion := "2.12.10",
    libraryDependencies ++= Seq(
      "com.michaelpollmeier" %% "gremlin-scala" % gremlinV withSources()
    )
  )

