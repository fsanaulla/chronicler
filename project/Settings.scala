import com.typesafe.sbt.SbtPgp.autoImportImpl.useGpg
import sbt.Keys._
import sbt.librarymanagement.LibraryManagementSyntax
import sbt.{Developer, Opts, ScmInfo, url}

/** Basic sbt settings */
object Settings extends LibraryManagementSyntax {

   val common = Seq(
    scalaVersion := "2.12.6",
    organization := "com.github.fsanaulla",
    scalacOptions ++= Scalac.options(scalaVersion.value),
    crossScalaVersions := Seq("2.11.8", scalaVersion.value),
    homepage := Some(url("https://github.com/fsanaulla/chronicler")),
    licenses += "Apache-2.0" -> url("https://opensource.org/licenses/Apache-2.0"),
    developers += Developer(id = "fsanaulla", name = "Faiaz Sanaulla", email = "fayaz.sanaulla@gmail.com", url = url("https://github.com/fsanaulla")),
    parallelExecution in IntegrationTest := false
  )


  val publish = Seq(
    useGpg := true,
    publishArtifact in Test := false,
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/fsanaulla/chronicler"),
        "https://github.com/fsanaulla/chronicler.git"
      )
    ),
    pomIncludeRepository := (_ => false),
    publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
      else
        Opts.resolver.sonatypeStaging
    )
  )
}
