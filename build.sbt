name := "chronicler"

version := "0.1"

organization := "com.fsanaulla"

scalaVersion := "2.12.3"

crossScalaVersions := Seq("2.12.3", "2.11.8")

useGpg := true

pgpReadOnly := false

scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps"
)

homepage := Some(url("https://github.com/fsanaulla/chronicler"))

licenses += "MIT" -> url("https://opensource.org/licenses/MIT")

scmInfo := Some(
  ScmInfo(
    url("https://github.com/fsanaulla/chronicler"),
    "https://github.com/fsanaulla/chronicler.git")
)

developers += Developer(
  id = "fsanaulla",
  name = "Faiaz Sanaulla",
  email = "fayaz.sanaulla@gmail.com",
  url = url("https://github.com/fsanaulla")
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

publishMavenStyle := true