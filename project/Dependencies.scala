import sbt._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.08.17
  */
object Dependencies {

  object Versions {
    val sttp = "1.1.14"
    val akka = "2.5.12"
    val netty = "4.1.22.Final"
    val scalaTest = "3.0.5"
    val scalaCheck = "1.14.0"
    val chronicler = "0.3.0"
  }

  val scalaTest = "org.scalatest" %% "scalatest" % Versions.scalaTest
  val scalaCheck = "org.scalacheck" %% "scalacheck" % Versions.scalaCheck

  def macroDeps(scalaVersion: String): Seq[ModuleID] = Seq(
    "org.scala-lang"       %  "scala-reflect"         % scalaVersion,
    "org.scalacheck"       %% "scalacheck"            % Versions.scalaCheck % Test,
    "com.github.fsanaulla" %% "scalacheck-generators" % "0.1.0"             % Provided exclude("org.scala-lang", "scala-reflect")
  ) :+ scalaTest % Test

  // testing
  val itTestingDeps: Seq[ModuleID] = Seq(
    "org.jetbrains"        %  "annotations" % "15.0", // to solve evicted warning
    "org.testcontainers"   %  "influxdb"    % "1.7.3" exclude("org.jetbrains", "annotations")
  ) :+ scalaTest % Provided

  // core
  val coreDep = Seq(
    "com.beachape"   %% "enumeratum" % "1.5.13",
    "org.spire-math" %% "jawn-ast"   % "0.12.1"
  ) :+ scalaTest % Test

  // akka-http
  val akkaDep: List[ModuleID] = List(
    "com.typesafe.akka"    %% "akka-stream"             % Versions.akka % Provided,
    "com.typesafe.akka"    %% "akka-actor"              % Versions.akka % Provided,
    "com.typesafe.akka"    %% "akka-testkit"            % Versions.akka % "test,it",
    "com.typesafe.akka"    %% "akka-http"               % "10.1.1",

    // if, you want to use it by your own, publish this deps from tests library first
    "com.github.fsanaulla" %% "chronicler-it-testing"   % Versions.chronicler % IntegrationTest,
    "com.github.fsanaulla" %% "chronicler-unit-testing" % Versions.chronicler % "test,it"
  ) :+ scalaTest % "test,it"

  // async-http
  val asyncHttp: Seq[ModuleID] = Seq(
    "io.netty"              %  "netty-handler"                    % Versions.netty, // to solve evicted warning
    "com.softwaremill.sttp" %% "async-http-client-backend-future" % Versions.sttp exclude("io.netty", "netty-handler"),

    // if, you want to use it by your own, publish this deps from tests library first
    "com.github.fsanaulla"  %% "chronicler-it-testing"   % Versions.chronicler % IntegrationTest,
    "com.github.fsanaulla"  %% "chronicler-unit-testing" % Versions.chronicler % "test,it"
  ) :+ scalaTest % "test,it"

  // url-http
  val urlHttp: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp" %% "core" % Versions.sttp,

    // if, you want to use it by your own, publish this deps from tests library first
    "com.github.fsanaulla"  %% "chronicler-it-testing"   % Versions.chronicler % IntegrationTest,
    "com.github.fsanaulla"  %% "chronicler-unit-testing" % Versions.chronicler % "test,it"
  ) :+ scalaTest % "test,it"

  // udp
  val udpDep: Seq[ModuleID] =
    Seq(
      "com.github.fsanaulla"  %% "scalatest-embedinflux"   % "0.1.7",
      "com.github.fsanaulla"  %% "chronicler-url-http"     % Versions.chronicler,
      "com.github.fsanaulla"  %% "chronicler-unit-testing" % Versions.chronicler,
      scalaTest
    ) map (_ % IntegrationTest)
}
