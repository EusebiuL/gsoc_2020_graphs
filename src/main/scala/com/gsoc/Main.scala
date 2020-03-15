package com.gsoc

import com.gsoc.gremlin.GremlinGraph
import com.gsoc.model.Model
import com.gsoc.processor.AlertsProcessor
import org.apache.commons.configuration.PropertiesConfiguration

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object Main {

  def main(args: Array[String]): Unit = {
    implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

    for {
      conf <- Future.fromTry {
        Try {
          new PropertiesConfiguration("remote-graph.properties")
        }
      }.recoverWith {
        case e: Throwable => throw new RuntimeException(s"Exception when reading config file: ${e.getLocalizedMessage}")
      }

      graph = GremlinGraph[Model](conf)
      processor = new AlertsProcessor
    } yield processor.startProcessor[Model](graph)
  }

}
