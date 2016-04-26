package com.ns.flow

import akka.stream.scaladsl.{Sink, Flow}
import com.ns.acotrs.DataSnapshotPublisher._
import org.joda.time.DateTime

object SimpleFlows {
  case class TimedMsg(msg: UpdateMsg, timestamp: DateTime)

  def printSink[T] = Sink.foreach[T](println(_))

  val timestampedFlow = Flow[UpdateMsg]
    .map { msg => TimedMsg(msg, DateTime.now())}

  val currentTemperatureFlow = Flow[UpdateMsg]
    .filter { msg => msg.key == "ambient_temperature_c"}

}
