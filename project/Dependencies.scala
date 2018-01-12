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
  final val nameOf = "com.github.dwickern" %% "scala-nameof" % "1.0.3"
  final val netty = "io.netty" % "netty-all" % "4.1.19.Final"

  final val scalaTest = "org.scalatest" %% "scalatest" % Versions.scalaTest % "test"


  final val projectResolvers = Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.bintrayRepo("scalameta", "maven")
  )

  final val rootDependencies = Seq(
    akkaHttp,
    sprayJson,
    scalaMeta,
    scalaTest,
    compilerPlugin(paradise)
  )
}
