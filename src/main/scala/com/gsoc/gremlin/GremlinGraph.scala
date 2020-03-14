package com.gsoc.gremlin

import gremlin.scala._
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory

import scala.concurrent.{ExecutionContext, Future}

trait GraphOps[T] {

  def createGraph(vertices: Seq[T]): Future[ScalaGraph]

  def findLongestChain(graph: ScalaGraph): Future[Seq[Vertex]]

  def computeVertexDegree(vertex: Vertex, graph: ScalaGraph): Future[Degree]

  def computeNumberOfAdjacentVertexes(vertex: Vertex): Future[Long]

  def extractVertexSubgraph(vertex: Vertex): Future[ScalaGraph]

}


final class GremlinGraph(implicit ec: ExecutionContext) {

  implicit lazy val _graph: ScalaGraph = TinkerFactory.createModern.asScala

  def createGraph: Future[TraversalSource] = Future.apply{
    _graph.traversal
  }






}
