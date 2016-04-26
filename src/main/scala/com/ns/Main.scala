package com.ns

import java.util.Properties

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.actor.ActorPublisher
import akka.stream.scaladsl._
import com.firebase.client._
import com.ns.acotrs.DataSnapshotPublisher._
import com.ns.acotrs.DataSnapshotPublisher
import com.ns.firabase.{ThermostatsListener, LoggedAuthResultHandler, ReactiveListener}
import com.ns.flow.{Graphs, SimpleFlows}
import com.ns.flow.SimpleFlows._

object Main extends App {
  val props = new Properties()
  props.load(this.getClass.getClassLoader.getResourceAsStream("props.txt"))
  val firebaseURL = props.getProperty("firebase-url")
  val nestToken = props.getProperty("nest-token")

  implicit val system = ActorSystem("reactive-nest")
  implicit val materializer = ActorMaterializer()

  val publisherRef = system.actorOf(Props[DataSnapshotPublisher])
  val src = Source.fromPublisher(ActorPublisher[UpdateMsg](publisherRef))

  val flow = SimpleFlows.timestampedFlow
    .to(printSink)
    .runWith(src)

  val graph = Graphs.createSimpleGraph(src, printSink[TimedMsg])
  graph.run()

  val fb = new Firebase(firebaseURL)
  fb.authWithCustomToken(nestToken, LoggedAuthResultHandler())
  fb.child("devices/thermostats").addChildEventListener(new ThermostatsListener(fb, publisherRef))

}
