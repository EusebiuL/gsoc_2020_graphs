package com.gsoc

import java.net.URI
import java.nio.charset.StandardCharsets

import com.gsoc.csv.CsvParser
import com.gsoc.gremlin_graph.GremlinGraph
import com.gsoc.modelz.Model
import gremlin.scala.ScalaGraph

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

final class AlertsProcessor(implicit ec: ExecutionContext) {

  def startProcessor[T <: Model](graph: GremlinGraph[T]): Future[ScalaGraph] = {

    for {

      fileProcessor <- Future.fromTry {
        Try {
          scala.io.Source.fromURI(URI.create("~/gsoc_2020_graphs/data/alerts.csv"))(StandardCharsets.UTF_8)
        }.recoverWith {
          //FIXME: Add a better error
          case e: Throwable => throw new RuntimeException(s"Error when reading from file: ${e.getLocalizedMessage}")
        }
      }
      lines <- new CsvParser().parse(fileProcessor)
      constructedGraph <- graph.constructGraph(lines)
    } yield constructedGraph

  }

}
