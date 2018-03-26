import sbt._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.08.17
  */
object Dependencies {

  // core
  final val sprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.11"
  final val enums = "com.beachape" %% "enumeratum" % "1.5.13"

  // akka-http
  final val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.0.11"

  // async-http
  final val asyncHttp = "com.softwaremill.sttp" %% "async-http-client-backend-future" % Versions.sttp

  // for testing
  final val scalaTest = "org.scalatest" %% "scalatest" % Versions.scalaTest % Test
  final val embedInflux = "com.github.fsanaulla" %% "scalatest-embedinflux" % Versions.scalaTestInflux % Test

  final val coreDep = Seq(sprayJson, enums, scalaTest, embedInflux)
}
