import com.jsuereth.sbtpgp.SbtPgp.autoImport._
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.headerLicense
import de.heikoseeberger.sbtheader.License
import sbt.Keys.{publishArtifact, _}
import sbt._
import sbt.librarymanagement.{Configurations, LibraryManagementSyntax}
import xerial.sbt.Sonatype.autoImport._

/** Basic sbt settings */
object Settings extends LibraryManagementSyntax {

  private val apacheUrl = "https://www.apache.org/licenses/LICENSE-2.0.txt"

  val CompileTimeIntegrationTest = config("it") extend Test
  val PropertyTest               = config("pt") extend Test

  val propertyTestSettings: Seq[Def.Setting[_]] =
    inConfig(PropertyTest)(Defaults.testSettings)

  private object Owner {
    val id     = "fsanaulla"
    val name   = "Faiaz Sanaulla"
    val email  = "fayaz.sanaulla@gmail.com"
    val github = "https://github.com/fsanaulla"
  }

  val common = Seq(
    scalaVersion := "2.13.1",
    organization := "com.github.fsanaulla",
    scalacOptions ++= Scalac.options(scalaVersion.value),
    crossScalaVersions := Seq("2.11.12", "2.12.10", scalaVersion.value),
    homepage := Some(url("https://github.com/fsanaulla/chronicler")),
    licenses += "Apache-2.0" -> url(apacheUrl),
    developers += Developer(
      id = Owner.id,
      name = Owner.name,
      email = Owner.email,
      url = url(Owner.github)
    ),
    makePomConfiguration := makePomConfiguration.value.withConfigurations(
      Configurations.defaultMavenConfigurations
    )
  )

  val publish = Seq(
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/fsanaulla/chronicler"),
        "scm:git@github.com:fsanaulla/chronicler.git"
      )
    ),
    pomIncludeRepository := (_ => false),
    publishTo := sonatypePublishToBundle.value,
    sonatypeBundleDirectory := (ThisBuild / baseDirectory).value / "target" / "sonatype-staging" / s"${version.value}",
    publishArtifact in Test := false,
    pgpPublicRing := file("pubring.asc"),
    pgpSecretRing := file("secring.asc"),
    pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toCharArray)
  )

  val header = headerLicense := Some(License.ALv2("2017-2019", Owner.name))
}
