import sbt._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.08.17
  */
object Dependencies {

  final val sprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % Versions.akkaHttp
  final val scalaTest = "org.scalatest" %% "scalatest" % Versions.scalaTest % Test

  final val scalaParser =  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.12.4"
  final val scalaReflect = "org.scala-lang" % "scala-reflect" % "2.12.4"

  final val asyncHttp = "com.softwaremill.sttp" %% "async-http-client-backend-future" % "1.1.4"
  final val akkaHttp = "com.typesafe.akka" %% "akka-http" % Versions.akkaHttp

  final val coreDep = Seq(sprayJson, scalaTest)
  final val akkaHttpDep = Seq(akkaHttp, scalaTest)
  final val asyncHttpDep = Seq(asyncHttp, scalaTest)
  final val macrosDep = Seq(scalaReflect, scalaTest)
}
