package com.ns.flow

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.testkit.TestProbe
import com.ns.acotrs.DataSnapshotPublisher.UpdateMsg
import com.ns.flow.SimpleFlows._
import org.scalatest.{Matchers, FunSuite}
import scala.concurrent.duration._

import scala.concurrent.Await

class FlowTest extends FunSuite with Matchers {
  implicit val system = ActorSystem("test-reactive-nest")
  implicit val materializer = ActorMaterializer()

  val testSource = Source(List(UpdateMsg("term1", "key1", "value1"),
    UpdateMsg("term1", "ambient_temperature_c", "33"), UpdateMsg("term1", "key1", "value1"),
    UpdateMsg("term2", "ambient_temperature_c", "22"), UpdateMsg("term1", "key1", "value1")))

  test("temperature filter test") {
    val future = testSource
        .via(SimpleFlows.currentTemperatureFlow)
        .runWith(Sink.fold(Seq.empty[UpdateMsg])(_ :+ _))
    val result = Await.result(future, 5.seconds)
    result should be (Seq(UpdateMsg("term1", "ambient_temperature_c", "33"), UpdateMsg("term2", "ambient_temperature_c", "22")))
  }

  test("graph test") {
    val probe = TestProbe()
    val testSink = Sink.foreach[TimedMsg](t => probe.ref ! t)
    val graph = Graphs.createSimpleGraph(testSource, testSink)
    graph.run()

    probe.expectNoMsg(1.second)
    probe.expectMsgPF(1500.milli, "expected 2 messages") {
      case TimedMsg(UpdateMsg("term1", "ambient_temperature_c", "33"), _) => true
      case TimedMsg(UpdateMsg("term2", "ambient_temperature_c", "22"), _) => true
    }
  }
}
