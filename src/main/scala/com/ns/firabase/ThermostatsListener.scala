package com.ns.firabase

import akka.actor.ActorRef
import com.firebase.client.{ChildEventListener, DataSnapshot, Firebase, FirebaseError}

import scala.collection.JavaConversions._
import scala.collection.concurrent.TrieMap

class ThermostatsListener(val fb: Firebase, val publisher: ActorRef) extends ChildEventListener {
  val deviceId = "device_id"
  val longName = "name_long"

  val thermostats = new TrieMap[String, ChildEventListener]()
  
  override def onChildRemoved(dataSnapshot: DataSnapshot): Unit = {
    val children = dataSnapshot.getChildren.toList
    val term = children.filter(_.getKey == deviceId).map(_.getValue.toString)
    term.foreach { id =>
      thermostats.get(id).foreach(l => fb.child(s"devices/thermostats/$id").removeEventListener(l))
      thermostats.remove(id)
      println(s"removed term: $id")
    }
  }

  override def onChildMoved(dataSnapshot: DataSnapshot, s: String): Unit = {}

  override def onChildChanged(dataSnapshot: DataSnapshot, s: String): Unit = {}

  override def onCancelled(firebaseError: FirebaseError): Unit = {
    println(s"error: $firebaseError")
  }

  override def onChildAdded(dataSnapshot: DataSnapshot, s: String): Unit = {
    val children = dataSnapshot.getChildren.toList
    val thermId = children.find(_.getKey == deviceId).map(_.getValue.toString)
    thermId.foreach { id =>
      val humanReadable = children.find(_.getKey == longName).map(_.getValue.toString) getOrElse id
      val listener = ReactiveListener(publisher, humanReadable)
      thermostats += (id -> listener)
      fb.child(s"devices/thermostats/$id").addChildEventListener(listener)

      println(s"added new term: $id --- $humanReadable")
    }
  }
}
