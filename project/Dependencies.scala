import sbt._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.08.17
  */
object Dependencies {

  object Versions {
    final val sttp = "1.1.14"
    final val akka = "2.5.12"
  }

  // core
  final val coreDep = Seq(
    "com.beachape"         %% "enumeratum"            % "1.5.13",
    "org.spire-math"       %% "jawn-ast"              % "0.12.1",
    "org.scalatest"        %% "scalatest"             % "3.0.5"   % Test,
    "com.github.fsanaulla" %% "scalatest-embedinflux" % "0.1.7"   % Test
  )

  // akka-http
  final val akkaDep = Seq(
    "com.typesafe.akka" %% "akka-stream"  % Versions.akka % Provided,
    "com.typesafe.akka" %% "akka-actor"   % Versions.akka % Provided,
    "com.typesafe.akka" %% "akka-testkit" % Versions.akka % Test,
    "com.typesafe.akka" %% "akka-http"    % "10.1.1"
  )

  // async-http
  final val asyncHttp = "com.softwaremill.sttp" %% "async-http-client-backend-future" % Versions.sttp

  // url-http
  final val urlHttp = "com.softwaremill.sttp" %% "core" % Versions.sttp

  // macros
  final def scalaReflect(scalaVersion: String): ModuleID = "org.scala-lang" % "scala-reflect" % scalaVersion
}
