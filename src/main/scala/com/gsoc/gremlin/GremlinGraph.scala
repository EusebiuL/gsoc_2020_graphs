package com.gsoc.gremlin

import com.gsoc.model.{Alert, Model}
import gremlin.scala._
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory

import scala.concurrent.{ExecutionContext, Future}

trait GraphOps[T <: Model] {

  def constructGraph(vertices: Seq[T]): Future[ScalaGraph]

  def findLongestChain(graph: ScalaGraph): Future[Seq[Vertex]]

  def computeVertexDegree(vertex: Vertex, graph: ScalaGraph): Future[Degree]

  def computeNumberOfAdjacentVertexes(vertex: Vertex): Future[Long]

  def extractVertexSubgraph(vertex: Vertex): Future[ScalaGraph]

}

final class GremlinGraph[T <: Model](private implicit val graph: ScalaGraph)(implicit ec: ExecutionContext)
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

  lazy val graph: ScalaGraph = TinkerFactory.createModern.asScala

  def apply[T](implicit ec: ExecutionContext) = new GremlinGraph[T](graph)

}
