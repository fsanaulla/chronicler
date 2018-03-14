import sbt._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.08.17
  */
object Dependencies {

  // core
  final val sprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % Versions.akkaHttp

  // akka-http
  final val akkaHttp = "com.typesafe.akka" %% "akka-http" % Versions.akkaHttp

  // async-http
  final val asyncHttp = "com.softwaremill.sttp" %% "async-http-client-backend-future" % Versions.sttp

  // macros
  final val scalaReflect = "org.scala-lang" % "scala-reflect" % "2.12.4"

  // for testing
  final val scalaTest = "org.scalatest" %% "scalatest" % Versions.scalaTest % Test
  final val embedInflux = "com.github.fsanaulla" %% "scalatest-embedinflux" % Versions.scalaTestInflux % Test


  final val coreDep = Seq(sprayJson, scalaTest, embedInflux)

  object Excluded {
    final val embeddedInflux = ExclusionRule("com.github.fsanaulla", "scalatest-embedinflux")
  }
}
