package com.gsoc.gremlin_graph

import com.gsoc.models.{Alert, Degree, Model}
import gremlin.scala._
import org.apache.commons.configuration.Configuration
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
//import org.janusgraph.core.JanusGraphFactory
import cats.implicits._

import scala.concurrent.{ExecutionContext, Future}

trait GraphOps[T <: Model] {

  def constructGraph(vertices: Seq[T]): Future[ScalaGraph]

  def findLongestChain(graph: ScalaGraph): Future[Seq[Vertex]]

  def computeVertexDegree(vertexLabel: Vertex): Future[Degree]

  def computeNumberOfAdjacentVertices(vertex: Vertex): Future[Long]

  def extractVertexSubgraph(vertex: Vertex): Future[ScalaGraph]

}

final class GremlinGraph[T <: Model](implicit val graph: ScalaGraph, ec: ExecutionContext) extends GraphOps[T] {

  override def constructGraph(vertices: Seq[T]): Future[ScalaGraph] = Future {
    vertices.map {
      //TODO: add conditions for when the value read is None
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

  override def computeVertexDegree(vertex: Vertex): Future[Degree] =
    for {
      inDegreeList <- vertex.inE.count.dedup().promise
      outDegreeList <- vertex.outE.count.promise
      inDegree = inDegreeList.head
      outDegree = outDegreeList.head
    } yield Degree(inDegree, outDegree, inDegree + outDegree)

  override def computeNumberOfAdjacentVertices(vertex: Vertex): Future[Long] = ???

  override def extractVertexSubgraph(vertex: Vertex): Future[ScalaGraph] = ???

}

object GremlinGraph {

  def graph(conf: Configuration): ScalaGraph =
    TinkerGraph
      .open(conf)
      .asScala //FIXME: add JanusGraphFactory.open(conf).asScala once I figure out the problem for "Packet <len12343123123> is out of range" o.O

  def apply[T <: Model](conf: Configuration)(implicit ec: ExecutionContext): Future[GremlinGraph[T]] = {
    implicit val graphParam: ScalaGraph = graph(conf)
    Future.successful(new GremlinGraph[T])
  }

}
