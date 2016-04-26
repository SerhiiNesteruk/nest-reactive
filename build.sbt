name := "nest-streaming"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq (
  "com.typesafe.akka" %% "akka-stream-experimental" % "2.0.2",
  "com.firebase" % "firebase-client-jvm" % "2.5.0",
  "joda-time" % "joda-time" % "2.9.1",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.1" % "test"
)
    