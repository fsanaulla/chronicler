name := "influx-scala-client"

version := "1.0"

organization := "com.fsanaulla"

scalaVersion := "2.12.0"

lazy val Versions = new {
  val akka = "10.0.8"
}

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-http_2.12" % Versions.akka
)
        