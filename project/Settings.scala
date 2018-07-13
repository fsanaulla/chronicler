import com.typesafe.sbt.SbtPgp.autoImportImpl.useGpg
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.headerLicense
import de.heikoseeberger.sbtheader.License
import sbt.Keys._
import sbt.librarymanagement.LibraryManagementSyntax
import sbt.{Developer, Opts, ScmInfo, url}

/** Basic sbt settings */
object Settings extends LibraryManagementSyntax {

  private val apacheUrl = "https://www.apache.org/licenses/LICENSE-2.0.txt"

  private object Owner {
    val id = "fsanaulla"
    val name = "Faiaz Sanaulla"
    val email = "fayaz.sanaulla@gmail.com"
    val github = "https://github.com/fsanaulla"
  }

   val common = Seq(
     scalaVersion := "2.12.6",
     organization := "com.github.fsanaulla",
     scalacOptions ++= Scalac.options(scalaVersion.value),
     crossScalaVersions := Seq("2.11.8", scalaVersion.value),
     homepage := Some(url("https://github.com/fsanaulla/chronicler")),
     licenses += "Apache-2.0" -> url(apacheUrl),
     developers += Developer(
       id = Owner.id,
       name = Owner.name,
       email = Owner.email,
       url = url(Owner.github)
     ),
     parallelExecution in IntegrationTest := false,
     publishArtifact in IntegrationTest := false,
     publishArtifact in Test := false
   )


  val publish = Seq(
    useGpg := true,
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/fsanaulla/chronicler"),
        "scm:git@github.com:fsanaulla/chronicler.git"
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

  val header = headerLicense := Some(License.ALv2("2017-2018", Owner.name))
}
