import sbt._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.08.17
  */
object Library {

  object Versions {
    val sttp    = "1.6.1"
    val netty   = "4.1.37.Final"
    val request = "0.2.0"

    object Akka {
      val akka     = "2.5.23"
      val akkaHttp = "10.1.7"
    }

    object Testing {
      val scalaTest            = "3.0.8"
      val scalaCheck           = "1.14.0"
      val scalaCheckGenerators = "0.2.0"
    }
  }

  val sttp        = "com.softwaremill.sttp" %% "core"         % Versions.sttp
  val scalaTest   = "org.scalatest"         %% "scalatest"    % Versions.Testing.scalaTest
  val scalaCheck  = "org.scalacheck"        %% "scalacheck"   % Versions.Testing.scalaCheck
  val akkaTestKit = "com.typesafe.akka"     %% "akka-testkit" % Versions.Akka.akka

  def macroDeps(scalaVersion: String): List[ModuleID] =
    List(
      "org.scala-lang"                      % "scala-reflect" % scalaVersion
    ) ::: List(scalaTest, scalaCheck).map(_ % Scope.test)

  // testing
  val testingDeps: List[ModuleID] = List(
    scalaTest,
    "org.jetbrains"      % "annotations" % "16.0.3",
    "org.testcontainers" % "influxdb"    % "1.11.3" exclude ("org.jetbrains", "annotations") exclude ("org.slf4j", "slf4j-api"),
    "org.slf4j"          % "slf4j-api"   % "1.7.25"
  )

  // core
  val coreDep: List[ModuleID] = List(
    "com.beachape"                        %% "enumeratum" % "1.5.13",
    "org.typelevel"                       %% "jawn-ast" % "0.14.2"
  ) ::: List(scalaTest, scalaCheck).map(_ % Scope.test)

  // akka-http
  val akkaDep: List[ModuleID] = List(
    "com.typesafe.akka" %% "akka-stream" % Versions.Akka.akka exclude ("com.typesafe", "config"),
    "com.typesafe"      % "config"       % "1.3.4",
//    "com.softwaremill.sttp" %% "akka-http-backend" % Versions.sttp,
    "com.typesafe.akka" %% "akka-http" % "10.1.9",
    akkaTestKit         % Scope.test
  )

  // async-http
  val asyncDeps: List[ModuleID] = List(
    "io.netty"              % "netty-handler"                     % Versions.netty,
    "com.softwaremill.sttp" %% "async-http-client-backend-future" % Versions.sttp exclude ("io.netty", "netty-handler") exclude ("org.reactivestreams", "reactive-streams"),
    "org.reactivestreams"   % "reactive-streams"                  % "1.0.2"
  )

  // looks like a shit, but need to keep it until spark on 2.12 will become stable
  def requestScala(scalaVersion: String): ModuleID = {
    val requestVersion = scalaVersion match {
      case v if v.startsWith("2.11") => "0.1.9"
      case _                         => Versions.request
    }

    "com.lihaoyi" %% "requests" % requestVersion
  }
}
