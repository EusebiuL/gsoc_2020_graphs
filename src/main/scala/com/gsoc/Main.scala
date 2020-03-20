package com.gsoc

import com.gsoc.gremlin_graph.GremlinGraph
import com.gsoc.models.Alert
import com.gsoc.processor.AlertsProcessor
import org.apache.commons.configuration.PropertiesConfiguration
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph
import org.slf4j.{Logger, LoggerFactory}
import gremlin.scala._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object Main {

  def main(args: Array[String]): Unit = {
    implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
    val logger: Logger = LoggerFactory.getLogger(getClass)

    val result = for {
      conf <-  Future.fromTry {
        Try {
          new PropertiesConfiguration("remote-graph.properties")
        }
      }.recoverWith {
        case e: Throwable => throw new RuntimeException(s"Exception when reading config file: ${e.getLocalizedMessage}")
      }

      graph =  GremlinGraph[Alert](conf)
      processor = new AlertsProcessor()
      result <- processor.startProcessor[Alert](graph)(Alert.alertReads)
    } yield result
    result.onComplete  {
      case Success(value) => logger.info(value.traversal.V.valueMap().toString())
      case Failure(exception) => throw new RuntimeException(exception.getLocalizedMessage)
    }
  }

}
