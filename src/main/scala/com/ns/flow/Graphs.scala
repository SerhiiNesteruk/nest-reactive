package com.ns.flow

import akka.stream.ClosedShape
import akka.stream.scaladsl._
import com.ns.acotrs.DataSnapshotPublisher._
import com.ns.flow.SimpleFlows.TimedMsg

import scala.concurrent.Future

object Graphs {
  def createSimpleGraph(in: Source[UpdateMsg, Unit],
                        out: Sink[TimedMsg, Future[Unit]]) = {
    val graph = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[Unit] =>
      import GraphDSL.Implicits._

      val balancer = builder.add(Balance[UpdateMsg](2))
      val heavyFlow1 = Flow[UpdateMsg].map{m => Thread.sleep(1000); println("heavy op 1 " + m); m}
      val heavyFlow2 = Flow[UpdateMsg].map{m => Thread.sleep(1000); println("heavy op 2 " + m); m}
      val merge = builder.add(Merge[UpdateMsg](2))

      val filterFlow = SimpleFlows.currentTemperatureFlow
      val enrich = SimpleFlows.timestampedFlow

      in ~> filterFlow ~> balancer ~> heavyFlow1 ~> merge ~> enrich ~> out
                          balancer ~> heavyFlow2 ~> merge
      ClosedShape
    })
    graph
  }
}
