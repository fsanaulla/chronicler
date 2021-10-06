import sbt._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.08.17
  */
object Library {

  object Versions {
    val request = "0.2.0"

    object Akka {
      val akka     = "2.5.32"
      val akkaHttp = "10.1.14"
    }

    object Testing {
      val scalaTest            = "3.2.10"
      val scalaCheck           = "1.14.0"
      val scalaCheckGenerators = "0.2.0"
    }
  }

  val scalaTest   = List("org.scalatest" %% "scalatest").map(_ % Versions.Testing.scalaTest)
  val scalaCheck  = "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0"
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % Versions.Akka.akka

  // testing
  val testingDeps: List[ModuleID] = List(
    "com.dimafeng" %% "testcontainers-scala" % "0.39.5"
  ) ++ scalaTest

  // akka-http
  // format: off
  val akkaDep: List[ModuleID] = List(
    "com.typesafe.akka" %% "akka-stream" % Versions.Akka.akka exclude ("com.typesafe", "config"),
    "com.typesafe"      %  "config"       % "1.4.1",
    "com.typesafe.akka" %% "akka-http"   % Versions.Akka.akkaHttp,
    akkaTestKit % Test
  )

  // async-http
  val asyncDeps: List[ModuleID] = List(
    "org.asynchttpclient"    % "async-http-client"   % "2.12.3",
    "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.1"
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
