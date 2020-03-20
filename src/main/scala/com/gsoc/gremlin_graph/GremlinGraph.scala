package com.gsoc.gremlin

import com.gsoc.model.{Alert, Model}
import gremlin.scala._
import org.apache.commons.configuration.Configuration
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph

import scala.concurrent.{ExecutionContext, Future}

trait GraphOps[T <: Model] {

  def constructGraph(vertices: Seq[T]): Future[ScalaGraph]

  def findLongestChain(graph: ScalaGraph): Future[Seq[Vertex]]

  def computeVertexDegree(vertex: Vertex, graph: ScalaGraph): Future[Degree]

  def computeNumberOfAdjacentVertexes(vertex: Vertex): Future[Long]

  def extractVertexSubgraph(vertex: Vertex): Future[ScalaGraph]

}

final class GremlinGraph[T <: Model](implicit val graph: ScalaGraph, ec: ExecutionContext)
    extends GraphOps[T] {

  override def constructGraph(vertices: Seq[T]): Future[ScalaGraph] = Future.successful {
    vertices.map {
      case alert: Alert => {
        //add vertices
        val firstVertex = graph + alert.field1
        val secondVertex = graph + alert.field2
        val thirdVertex = graph + alert.field3

        //add edges
        secondVertex --- "is" --> firstVertex
        thirdVertex --- "is" --> firstVertex
        secondVertex --- "knows" --> thirdVertex

      }
      case _ => throw new RuntimeException("Wrong model")
    }
    graph
  }

  override def findLongestChain(graph: ScalaGraph): Future[Seq[Vertex]] = ???

  override def computeVertexDegree(vertex: Vertex, graph: ScalaGraph): Future[Degree] = ???

  override def computeNumberOfAdjacentVertexes(vertex: Vertex): Future[Long] = ???

  override def extractVertexSubgraph(vertex: Vertex): Future[ScalaGraph] = ???

}

object GremlinGraph {

  def graph(conf: Configuration): ScalaGraph = EmptyGraph.instance().asScala  //GraphFactory.open(conf).asScala // TinkerGraph.open(conf).asScala

  def apply[T <: Model](conf: Configuration)(implicit ec: ExecutionContext): GremlinGraph[T] = {
    implicit val graphParam: ScalaGraph = graph(conf)
    new GremlinGraph[T]
  }

}
