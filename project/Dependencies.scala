import sbt._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.08.17
  */
object Dependencies {

  // core
  final val coreDep = Seq(
    "com.beachape"         %% "enumeratum"            % "1.5.13",
    "org.spire-math"       %% "jawn-ast"              % "0.12.1",
    "org.scalatest"        %% "scalatest"             % "3.0.5"   % Test,
    "com.github.fsanaulla" %% "scalatest-embedinflux" % "0.1.7"   % Test
  )

  // akka-http
  final val akkaDep = Seq(
    "com.typesafe.akka" %% "akka-stream" % "2.5.11" % Provided,
    "com.typesafe.akka" %% "akka-http"   % "10.1.1"
  )

  // async-http
  final val asyncHttp = "com.softwaremill.sttp" %% "async-http-client-backend-future" % "1.1.12"

  // macros
  final def scalaReflect(scalaVersion: String): ModuleID = "org.scala-lang" % "scala-reflect" % scalaVersion
}
