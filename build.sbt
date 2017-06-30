name := "influx-scala-client"
version := "1.0"
scalaVersion := "2.12.0"

lazy val Versions = new {
  val akka = "10.0.8"
}

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1",
  "com.typesafe.akka" % "akka-http_2.12" % Versions.akka,
  "com.typesafe.akka" % "akka-http-spray-json_2.12" % Versions.akka,
  "com.typesafe.akka" % "akka-http-testkit_2.12" % Versions.akka,
  "com.typesafe.scala-logging" % "scala-logging_2.12" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalatest" % "scalatest_2.12" % "3.0.3" % Test
)
        