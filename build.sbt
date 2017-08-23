name := "influxdb-scala-client"

version := "0.1"

organization := "com.fsanaulla"

scalaVersion := "2.12.0"

scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps"
)

lazy val Versions = new {
  val akkaHttp = "10.0.9"
}

libraryDependencies ++= Seq(
  "com.typesafe.akka"   %%   "akka-http"                    %   Versions.akkaHttp,
  "org.scalatest"       %%   "scalatest"                    %   "3.0.3"             % "test",
  "com.typesafe.akka"   %%   "akka-http-spray-json"         %   Versions.akkaHttp
)

coverageMinimum := 75
coverageExcludedPackages := "com\\.fsanaulla\\.utils.*;com\\.fsanaulla\\.model.*"