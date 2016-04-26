package com.ns.acotrs

import akka.stream.actor.ActorPublisher

import scala.annotation.tailrec

class DataSnapshotPublisher extends ActorPublisher[DataSnapshotPublisher.UpdateMsg] {
  import akka.stream.actor.ActorPublisherMessage._
  import DataSnapshotPublisher._
  val MaxBufferSize = 100
  var buf = Vector.empty[UpdateMsg]

  def receive = {
    case updMsg: UpdateMsg if buf.size == MaxBufferSize =>
      sender() ! MsgDenied
    case updMsg: UpdateMsg =>
      sender() ! MsgAccepeted
      if (buf.isEmpty && totalDemand > 0)
        onNext(updMsg)
      else {
        buf :+= updMsg
        deliverBuf()
      }
    case Request(_) =>
      deliverBuf()
    case Cancel =>
      context.stop(self)
  }

  @tailrec final def deliverBuf(): Unit =
    if (totalDemand > 0) {
      /*
       * totalDemand is a Long and could be larger than
       * what buf.splitAt can accept
       */
      if (totalDemand <= Int.MaxValue) {
        val (use, keep) = buf.splitAt(totalDemand.toInt)
        buf = keep
        use foreach onNext
      } else {
        val (use, keep) = buf.splitAt(Int.MaxValue)
        buf = keep
        use foreach onNext
        deliverBuf()
      }
    }
}

object DataSnapshotPublisher {
  case class UpdateMsg(from: String, key: String, value: String)

  case object MsgAccepeted
  case object MsgDenied
}
