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
    "com.softwaremill.sttp.client3" %% "akka-http-backend" % "3.3.14",
    "com.typesafe.akka" %% "akka-stream" % Versions.Akka.akka exclude ("com.typesafe", "config"),
    akkaTestKit % Test
  )

  // async-http
  val asyncDeps: List[ModuleID] = List(
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-future" % "3.3.14"
  )
}
