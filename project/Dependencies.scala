import sbt._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.08.17
  */
object Dependencies {

  object Versions {
    val sttp       = "1.1.14"
    val netty      = "4.1.22.Final"
    val testing    = "0.1.0"

    object Akka {
      val akka = "2.5.12"
      val akkaHttp = "10.1.1"
    }

    object Testing {
      val scalaTest  = "3.0.5"
      val scalaCheck = "1.14.0"
    }
  }

  val scalaTest  = "org.scalatest"  %% "scalatest"  % Versions.Testing.scalaTest
  val scalaCheck = "org.scalacheck" %% "scalacheck" % Versions.Testing.scalaCheck

  val httpClientTesting = List(
    // if, you want to use it by your own, publish this deps from tests library first
    "com.github.fsanaulla"  %% "chronicler-it-testing"   % Versions.testing % Scope.it,
    "com.github.fsanaulla"  %% "chronicler-unit-testing" % Versions.testing % Scope.all
  )

  def macroDeps(scalaVersion: String): List[ModuleID] = List(
    "org.scala-lang"       %  "scala-reflect"         % scalaVersion,
    "com.github.fsanaulla" %% "scalacheck-generators" % "0.1.3" % Scope.test exclude("org.scala-lang", "scala-reflect")
  ) ::: List(scalaTest, scalaCheck).map(_ % Scope.test)

  // testing
  val itTestingDeps: Seq[ModuleID] = Seq(
    "org.jetbrains"        %  "annotations" % "15.0", // to solve evicted warning
    "org.testcontainers"   %  "influxdb"    % "1.7.3" exclude("org.jetbrains", "annotations")
  ) :+ scalaTest % Scope.compileTimeOnly

  // core
  val coreDep: List[ModuleID] = List(
    "com.beachape"   %% "enumeratum" % "1.5.13",
    "org.spire-math" %% "jawn-ast"   % "0.12.1"
  ) ::: List(scalaTest, scalaCheck).map(_ % Scope.test)

  // akka-http
  val akkaDep: List[ModuleID] = List(
    "com.typesafe.akka"    %% "akka-stream"  % Versions.Akka.akka     % Scope.compileTimeOnly,
    "com.typesafe.akka"    %% "akka-actor"   % Versions.Akka.akka     % Scope.compileTimeOnly,
    "com.typesafe.akka"    %% "akka-testkit" % Versions.Akka.akka     % Scope.all,
    "com.typesafe.akka"    %% "akka-http"    % Versions.Akka.akkaHttp
  ) ::: httpClientTesting

  // async-http
  val asyncHttp: Seq[ModuleID] = List(
    "io.netty"              %  "netty-handler"                    % Versions.netty, // to solve evicted warning
    "com.softwaremill.sttp" %% "async-http-client-backend-future" % Versions.sttp exclude("io.netty", "netty-handler")
  ) ::: httpClientTesting

  // url-http
  val urlHttp: Seq[ModuleID] = List(
    "com.softwaremill.sttp" %% "core" % Versions.sttp
  ) ::: httpClientTesting

  // udp
  val udpDep: Seq[ModuleID] =
    Seq(
      "com.github.fsanaulla"  %% "scalatest-embedinflux"   % "0.1.7",
      "com.github.fsanaulla"  %% "chronicler-url-http"     % "0.3.3",
      "com.github.fsanaulla"  %% "chronicler-unit-testing" % Versions.testing,
      scalaTest
    ) map (_ % Scope.it)
}
