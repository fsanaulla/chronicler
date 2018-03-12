import sbt._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.08.17
  */
object Dependencies {

  final val sprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % Versions.akkaHttp
  final val akkaHttp = "com.typesafe.akka" %% "akka-http" % Versions.akkaHttp

  final val asyncHttp = "com.softwaremill.sttp" %% "async-http-client-backend-future" % Versions.sttp

  final val scalaTest = "org.scalatest" %% "scalatest" % Versions.scalaTest % Test
  final val embedInflux = "com.github.fsanaulla" %% "scalatest-embedinflux" % "0.1.3" % Test

  final val scalaReflect = "org.scala-lang" % "scala-reflect" % "2.12.4"

  final val coreDep = Seq(sprayJson, scalaTest, embedInflux)

  object Excluded {
    final val embeddedInflux = ExclusionRule("com.github.fsanaulla", "scalatest-embedinflux")
  }
}
