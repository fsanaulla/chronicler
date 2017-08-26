name := "chronicler"

version := "0.1"

organization := "com.fsanaulla"

scalaVersion := "2.12.3"

scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka"   %%   "akka-http"              %   Versions.akkaHttp,
  "com.typesafe.akka"   %%   "akka-http-spray-json"   %   Versions.akkaHttp,
  "org.scalatest"       %%   "scalatest"              %   Versions.scalaTest   % "test",
  "com.storm-enroute"   %%   "scalameter"             %   Versions.scalaMeter  % "test"
)

coverageMinimum := 90

coverageExcludedPackages := "" +
  "com\\.fsanaulla\\.utils.*;" +
  "com\\.fsanaulla\\.model.*;" +
  "com\\.fsanaulla\\.InfluxClient.*"