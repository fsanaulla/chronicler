import sbt._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.08.17
  */
object Dependencies {

  object Versions {
    val sttp       = "1.5.17"
    val netty      = "4.1.36.Final"

    object Akka {
      val akka     = "2.5.22"
      val akkaHttp = "10.1.7"
    }

    object Testing {
      val scalaTest            = "3.0.7"
      val scalaCheck           = "1.14.0"
      val scalaCheckGenerators = "0.2.0"
    }
  }

  val sttp        = "com.softwaremill.sttp" %% "core"         % Versions.sttp
  val scalaTest   = "org.scalatest"         %% "scalatest"    % Versions.Testing.scalaTest
  val scalaCheck  = "org.scalacheck"        %% "scalacheck"   % Versions.Testing.scalaCheck
  val akkaTestKit = "com.typesafe.akka"     %% "akka-testkit" % Versions.Akka.akka

  val scalaCheckGenerators =
    "com.github.fsanaulla" %% "scalacheck-generators" % Versions.Testing.scalaCheckGenerators exclude("org.scala-lang", "scala-reflect")

  def macroDeps(scalaVersion: String): List[ModuleID] = List(
    "org.scala-lang"       %  "scala-reflect"         % scalaVersion,
    "com.github.fsanaulla" %% "scalacheck-generators" % "0.2.0"       % Scope.test exclude("org.scala-lang", "scala-reflect")
  ) ::: List(scalaTest, scalaCheck).map(_ % Scope.test)

  // testing
  val testingDeps: List[ModuleID] = List(
    scalaTest,
    "org.jetbrains"      % "annotations" % "16.0.3",
    "org.testcontainers" % "influxdb"    % "1.10.6"   exclude("org.jetbrains", "annotations") exclude("org.slf4j", "slf4j-api"),
    "org.slf4j"          % "slf4j-api"   % "1.7.25"
  )

  // core
  val coreDep: List[ModuleID] = List(
    "com.beachape"   %% "enumeratum" % "1.5.13",
    "org.spire-math" %% "jawn-ast"   % "0.13.0"
  ) ::: List(scalaTest, scalaCheck, scalaCheckGenerators).map(_ % Scope.test)

  // akka-http
  val akkaDep: List[ModuleID] = List(
    "com.typesafe.akka"     %% "akka-stream"       % Versions.Akka.akka exclude("com.typesafe", "config"),
    "com.typesafe"          %  "config"            % "1.3.4",
    "com.softwaremill.sttp" %% "akka-http-backend" % Versions.sttp,
    akkaTestKit % Scope.test
  )

  // async-http
  val asyncDeps: List[ModuleID] = List(
    "io.netty"              %  "netty-handler"                    % Versions.netty,
    "com.softwaremill.sttp" %% "async-http-client-backend-future" % Versions.sttp   exclude("io.netty", "netty-handler") exclude("org.reactivestreams", "reactive-streams"),
    "org.reactivestreams"   %  "reactive-streams"                 % "1.0.2"
  )
}
