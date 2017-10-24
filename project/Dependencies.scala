import sbt._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.08.17
  */
object Dependencies {

  final val scalaMeta = "org.scalameta" %% "scalameta" % Versions.scalaMeta
  final val paradise = "org.scalameta" % "paradise" % "3.0.0-M8" cross CrossVersion.full
  final val sprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % Versions.akkaHttp
  final val akkaHttp = "com.typesafe.akka" %% "akka-http" % Versions.akkaHttp
  final val scalaTest = "org.scalatest" %% "scalatest" % Versions.scalaTest % "test"
  final val scalaMeter = "com.storm-enroute" %% "scalameter" % Versions.scalaMeter % "test"


  final val projectResolvers = Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.bintrayRepo("scalameta", "maven")
  )

  final val macrosDependencies = Seq(scalaMeta, sprayJson)

  final val rootDependencies = Seq(akkaHttp, scalaTest, scalaMeter)
}
