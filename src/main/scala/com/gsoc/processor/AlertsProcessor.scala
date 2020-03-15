package com.gsoc.processor

import java.net.URI
import java.nio.charset.StandardCharsets

import com.gsoc.csv.CsvParser
import com.gsoc.gremlin.GremlinGraph
import com.gsoc.model.Model
import gremlin.scala.ScalaGraph
import zamblauskas.csv.parser.ColumnReads

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

final class AlertsProcessor(implicit ec: ExecutionContext) {

  def startProcessor[T <: Model](graph: GremlinGraph[T])(implicit cr: ColumnReads[T]): Future[ScalaGraph] = {

    for {

      fileProcessor <- Future.fromTry {
        Try {
          scala.io.Source.fromURI(URI.create("./data/alerts.csv"))(StandardCharsets.UTF_8)
        }.recoverWith {
          //FIXME: Add a better error
          case e: Throwable => throw new RuntimeException(s"Error when reading from file: ${e.getLocalizedMessage}")
        }
      }
      fileAsString = fileProcessor.mkString
      parsedModel <- Future.successful {
        new CsvParser[T].parse(fileAsString)
      }
      model <- parsedModel.fold(
        _ => Future.failed[Seq[T]](new RuntimeException("Failed at parsing csv")),
        sm => Future.successful[Seq[T]](sm))
      constructedGraph <- graph.constructGraph(model)
    } yield constructedGraph

  }

}
