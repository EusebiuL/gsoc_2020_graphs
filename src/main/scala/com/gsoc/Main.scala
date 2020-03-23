package com.gsoc

import java.util.concurrent.Executors

import com.gsoc.gremlin_graph.GremlinGraph
import com.gsoc.models.Alert
import com.gsoc.processor.AlertsProcessor
import org.apache.commons.configuration.PropertiesConfiguration
import org.apache.tinkerpop.gremlin.structure.Direction
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

import collection.JavaConverters._

object Main {

  def main(args: Array[String]): Unit = {
    implicit val ec
      : ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(3)) //TODO: make this configurable
    val logger: Logger = LoggerFactory.getLogger(getClass)

    val result = for {
      conf <- Future.fromTry {
        Try {
          new PropertiesConfiguration("remote-graph.properties")
        }
      }.recoverWith {
        case e: Throwable => throw new RuntimeException(s"Exception when reading config file: ${e.getLocalizedMessage}")
      }

      graph <- GremlinGraph[Alert](conf)
      processor = new AlertsProcessor()
      result <- processor.startProcessor[Alert](graph)(Alert.alertReads)
    } yield result
    result.onComplete {
      case Success(value) => {
        value.traversal.V.toList.foreach { vertex =>
          val edges = vertex.edges(Direction.OUT).asScala
          edges.foreach { edge =>
            val vertexLabel = vertex.label()
            val edgeLabel = edge.label()
            val otherVertexLabel = edge.inVertex().label()
            logger.info(s"""$vertexLabel --"$edgeLabel" --> $otherVertexLabel \n""")
            //TODO: refactor this to a method in GremlinGraph
          }
        }
        sys.exit(0)
      }
      case Failure(exception) => {
        throw new RuntimeException(exception.getLocalizedMessage)
        sys.exit(0)

      }
    }

  }

}
