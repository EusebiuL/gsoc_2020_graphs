package com.gsoc.processor

import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.util.logging.Logger

import com.gsoc.csv.CsvParser
import com.gsoc.gremlin_graph.GremlinGraph
import com.gsoc.models.Model
import gremlin.scala.ScalaGraph
import zamblauskas.csv.parser.ColumnReads
import cats.implicits._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

final class AlertsProcessor(implicit ec: ExecutionContext) {

  private[this] val logger = Logger.getLogger(getClass.getCanonicalName)

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
      lines <- Future { new CsvParser[T]().parse(fileAsString) }
      parsedLines <- lines.fold(e => Future.failed(new RuntimeException(e.message)), s => Future.successful(s))
      constructedGraph <- graph.constructGraph(parsedLines)
      vertexList = constructedGraph.traversal.V.toList()
      _ <- vertexList.traverse { vertex =>
        for {
          degree <- graph.computeVertexDegree(vertex)
          _ <- Future { logger.info(s"Degree for vertex $vertex: $degree") }

        } yield degree
      }
    } yield constructedGraph

  }

}
