package com.ns.firabase

import akka.actor.ActorRef
import com.firebase.client.{FirebaseError, DataSnapshot, ChildEventListener}
import com.ns.acotrs.DataSnapshotPublisher.UpdateMsg

class ReactiveListener(val publisher: ActorRef, val termName: String) extends ChildEventListener{
  override def onChildRemoved(dataSnapshot: DataSnapshot): Unit = {
//    println("child removed")
  }

  override def onChildMoved(dataSnapshot: DataSnapshot, s: String): Unit = {
//    println("child moved")
  }

  override def onChildChanged(dataSnapshot: DataSnapshot, s: String): Unit = {
    val updMsg = UpdateMsg(termName, dataSnapshot.getKey, dataSnapshot.getValue.toString)
    publisher ! updMsg
  }

  override def onCancelled(firebaseError: FirebaseError): Unit = {
    println(s"$termName error raised $firebaseError")
  }

  override def onChildAdded(dataSnapshot: DataSnapshot, s: String): Unit = {
//    println("child added")
  }
}

object ReactiveListener {
  def apply(publisher: ActorRef, name: String) = new ReactiveListener(publisher, name)
}
