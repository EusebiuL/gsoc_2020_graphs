package com.gsoc.processor

import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.{Path, Paths}

import com.gsoc.csv.CsvParser
import com.gsoc.gremlin_graph.GremlinGraph
import com.gsoc.models.Model
import gremlin.scala.ScalaGraph
import zamblauskas.csv.parser.ColumnReads

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

final class AlertsProcessor(implicit ec: ExecutionContext) {

  def startProcessor[T <: Model](graph: GremlinGraph[T])(implicit cr: ColumnReads[T]): Future[ScalaGraph] = {

    for {

      fileProcessor <- Future.fromTry {
        Try {
          scala.io.Source.fromURI(Paths.get("/Users/eusebiu/gsoc_2020_graphs/data/alerts.csv").toUri)(
            StandardCharsets.UTF_8)
        }.recoverWith {
          //FIXME: Add a better error
          case e: Throwable => throw new RuntimeException(s"Error when reading from file: ${e.getLocalizedMessage}")
        }
      }
      fileAsString = fileProcessor.mkString
      lines <- Future{ new CsvParser[T]().parse(fileAsString)) }
      parsedLines <- lines.fold(e => Failure(new RuntimeException(e.message)), s => Try(s))
      _ <- Try(println(parsedLines))
      constructedGraph <- Try(graph.constructGraph(parsedLines))
    } yield constructedGraph

  }

}
