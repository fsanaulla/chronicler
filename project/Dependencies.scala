import sbt._
/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.08.17
  */
object Dependencies {

  final val projectResolvers = Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.bintrayRepo("scalameta", "maven")
  )

  final val dep = Seq(
    "com.typesafe.akka"   %%   "akka-http"              %   Versions.akkaHttp,
    "com.typesafe.akka"   %%   "akka-http-spray-json"   %   Versions.akkaHttp,
    "org.scalameta"       %%   "scalameta"              %   Versions.scalaMeta,
    "org.scalatest"       %%   "scalatest"              %   Versions.scalaTest   % "test",
    "com.storm-enroute"   %%   "scalameter"             %   Versions.scalaMeter  % "test",
    compilerPlugin(
      "org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full)
  )
}
