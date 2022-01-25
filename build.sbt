import Owner._
import de.heikoseeberger.sbtheader.License
import sbt.Keys.{libraryDependencies, name}
import xerial.sbt.Sonatype._

ThisBuild / scalaVersion := "2.13.4"
ThisBuild / organization := "com.github.fsanaulla"
ThisBuild / description  := "Scala toolchain for InfluxDB "
ThisBuild / homepage     := Some(url(s"${Owner.github}/${Owner.projectName}"))
ThisBuild / developers += Developer(
  id = Owner.id,
  name = Owner.name,
  email = Owner.email,
  url = url(Owner.github)
)

// publish
ThisBuild / scmInfo := Some(
  ScmInfo(
    url(s"${Owner.github}/${Owner.projectName}"),
    s"scm:git@github.com:${Owner.id}/${Owner.projectName}.git"
  )
)
ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / sonatypeBundleDirectory := (ThisBuild / baseDirectory).value / "target" / "sonatype-staging" / s"${version.value}"
ThisBuild / sonatypeProjectHosting := Some(
  GitHubHosting(Owner.github, Owner.projectName, Owner.email)
)
ThisBuild / licenses      := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toCharArray)
ThisBuild / publishMavenStyle := true
ThisBuild / headerLicense     := Some(License.ALv2("2017-2021", Owner.name))

val scala213 = "2.13.8"
val scala212 = "2.12.15"
val scala211 = "2.11.12"

lazy val chronicler = project
  .in(file("."))
  .settings(publish / skip := true)
  .configure(license)
  .aggregate(
    Seq(
      coreIO,
      coreManagement,
      coreShared,
      ahcIO,
      ahcManagement,
      ahcShared,
      akkaIO,
      akkaManagement,
      akkaShared,
      urlIO,
      urlManagement,
      urlShared,
      macros,
      udp
    ).flatMap(_.projectRefs): _*
  )
  .enablePlugins(ScalafmtPlugin)

//////////////////////////////////////////////////////
//////////////////// CORE MODULES ////////////////////
//////////////////////////////////////////////////////
lazy val coreIO = projectMatrix
  .in(file("modules/core/io"))
  .settings(
    name := s"$projectName-core-io"
  )
  .dependsOn(coreShared)
  .jvmPlatform(scalaVersions = Seq(scala213, scala212, scala211))

lazy val coreManagement = projectMatrix
  .in(file("modules/core/management"))
  .settings(
    name := s"$projectName-core-management"
  )
  .dependsOn(coreShared)
  .jvmPlatform(scalaVersions = Seq(scala213, scala212, scala211))

lazy val coreShared = projectMatrix
  .in(file("modules/core/shared"))
  .settings(
    name := s"$projectName-core-shared",
    libraryDependencies ++= List(
      "com.beachape" %% "enumeratum" % "1.7.0",
      Library.jawnAst(scalaVersion.value)
    ) ++ (Library.scalaCheck :: Library.scalaTest).map(_ % Test)
  )
  .settings(Settings.propertyTestSettings: _*)
  .configs(Settings.PropertyTest)
  .jvmPlatform(scalaVersions = Seq(scala213, scala212, scala211))

//////////////////////////////////////////////////////
////////////////// URL HTTP MODULES //////////////////
//////////////////////////////////////////////////////
lazy val urlManagement = projectMatrix
  .in(file("modules/sync/management"))
  .settings(name := s"$projectName-url-management")
  .configure(defaultSettingsWithIt)
  .dependsOn(coreManagement, urlShared)
  .dependsOn(testing % "it,test")
  .jvmPlatform(scalaVersions = Seq(scala213, scala212, scala211))

lazy val urlIO = projectMatrix
  .in(file("modules/sync/io"))
  .settings(name := s"$projectName-url-io")
  .configure(defaultSettingsWithIt)
  .dependsOn(coreIO, urlShared)
  .dependsOn(urlManagement % "it,test")
  .dependsOn(macros % "it,test")
  .dependsOn(testing % "it,test")
  .jvmPlatform(scalaVersions = Seq(scala213, scala212, scala211))

lazy val urlShared = projectMatrix
  .in(file("modules/sync/shared"))
  .settings(
    name := s"$projectName-url-shared",
    libraryDependencies ++=
      "com.softwaremill.sttp.client3" %% "core" % "3.4.1"
        :: Library.scalaTest.map(_ % Test)
  )
  .dependsOn(coreShared)
  .dependsOn(testing % "test")
  .jvmPlatform(scalaVersions = Seq(scala213, scala212, scala211))

//////////////////////////////////////////////////////
////////////////// AKKA HTTP MODULES /////////////////
//////////////////////////////////////////////////////
lazy val akkaManagement = projectMatrix
  .in(file("modules/akka/management"))
  .settings(
    name                                      := s"$projectName-akka-management",
    libraryDependencies += Library.akkaTestKit % "test,it"
  )
  .configure(defaultSettingsWithIt)
  .dependsOn(coreManagement, akkaShared)
  .dependsOn(testing % "test,it")
  .jvmPlatform(scalaVersions = Seq(scala213, scala212))

lazy val akkaIO = projectMatrix
  .in(file("modules/akka/io"))
  .settings(
    name                                      := s"$projectName-akka-io",
    libraryDependencies += Library.akkaTestKit % "test,it"
  )
  .configure(defaultSettingsWithIt)
  .dependsOn(coreIO, akkaShared)
  .dependsOn(akkaManagement % "test,it")
  .dependsOn(testing % "test,it")
  .dependsOn(macros % "test,it")
  .jvmPlatform(scalaVersions = Seq(scala213, scala212))

lazy val akkaShared = projectMatrix
  .in(file("modules/akka/shared"))
  .settings(
    name := s"$projectName-akka-shared",
    libraryDependencies ++=
      Library.akkaDep ++ Library.scalaTest.map(_ % Test)
  )
  .dependsOn(coreShared)
  .dependsOn(testing % "test")
  .jvmPlatform(scalaVersions = Seq(scala213, scala212))

//////////////////////////////////////////////////////
///////////////// ASYNC HTTP MODULES /////////////////
//////////////////////////////////////////////////////
lazy val ahcManagement = projectMatrix
  .in(file("modules/async/management"))
  .settings(name := s"$projectName-ahc-management")
  .configure(defaultSettingsWithIt)
  .dependsOn(coreManagement, ahcShared)
  .dependsOn(testing % "it,test")
  .jvmPlatform(scalaVersions = Seq(scala213, scala212))

lazy val ahcIO = projectMatrix
  .in(file("modules/async/io"))
  .settings(name := s"$projectName-ahc-io")
  .configure(defaultSettingsWithIt)
  .dependsOn(coreIO, ahcShared)
  .dependsOn(ahcManagement % "it,test")
  .dependsOn(testing % "it,test")
  .jvmPlatform(scalaVersions = Seq(scala213, scala212))

lazy val ahcShared = projectMatrix
  .in(file("modules/async/shared"))
  .settings(
    name := s"$projectName-ahc-shared",
    libraryDependencies ++=
      Library.asyncDeps ++ Library.scalaTest.map(_ % Test)
  )
  .dependsOn(coreShared)
  .dependsOn(testing % "test")
  .jvmPlatform(scalaVersions = Seq(scala213, scala212))

//////////////////////////////////////////////////////
///////////////////// UPD MODULE /////////////////////
//////////////////////////////////////////////////////
lazy val udp = projectMatrix
  .in(file("modules/udp"))
  .settings(
    name := s"$projectName-udp"
  )
  .configure(defaultSettingsWithIt)
  .dependsOn(coreShared)
  .dependsOn(testing % "it,test")
  .dependsOn(urlIO % "it,test")
  .dependsOn(urlManagement % "it,test")
  .jvmPlatform(scalaVersions = Seq(scala213, scala212, scala211))

//////////////////////////////////////////////////////
///////////////////// MACRO MODULE ///////////////////
//////////////////////////////////////////////////////
lazy val macros = projectMatrix
  .in(file("modules/macros"))
  .settings(
    name := s"$projectName-macros",
    libraryDependencies ++= Seq(
      "org.scala-lang"                                   % "scala-reflect" % scalaVersion.value
    ) ++ (Library.scalaCheck :: Library.scalaTest).map(_ % Test)
  )
  .settings(Settings.propertyTestSettings: _*)
  .configs(Settings.PropertyTest)
  .dependsOn(coreShared)
  .dependsOn(testing % "test")
  .jvmPlatform(scalaVersions = Seq(scala213, scala212, scala211))

//////////////////////////////////////////////////////
/////////////////// TESTING MODULES //////////////////
//////////////////////////////////////////////////////
lazy val testing = projectMatrix
  .in(file("modules/testing"))
  .settings(
    name := s"$projectName-testing",
    libraryDependencies ++= Library.testingDeps
  )
  .dependsOn(coreShared)
  .jvmPlatform(scalaVersions = Seq(scala213, scala212, scala211))

//////////////////////////////////////////////////////
////////////////////// EXAMPLES //////////////////////
//////////////////////////////////////////////////////
lazy val akkaIOExample =
  exampleModule("akka-io-example", "akka/io", akkaIO.jvm(scala213), macros.jvm(scala213))

lazy val akkaManagementExample =
  exampleModule("akka-management-example", "akka/management", akkaManagement.jvm(scala213))

lazy val ahcIOExample =
  exampleModule("ahc-io-example", "async/io", ahcIO.jvm(scala213), macros.jvm(scala213))

lazy val ahcManagementExample =
  exampleModule("ahc-management-example", "async/management", ahcManagement.jvm(scala213))

lazy val urlIOExample =
  exampleModule("url-io-example", "url/io", urlIO.jvm(scala213), macros.jvm(scala213))

lazy val urlManagementExample =
  exampleModule("url-management-example", "url/management", urlManagement.jvm(scala213))

lazy val udpExample =
  exampleModule("udp-example", "udp", udp.jvm(scala213), macros.jvm(scala213))

//////////////////////////////////////////////////////
///////////////////// BENCHMARKS /////////////////////
//////////////////////////////////////////////////////
lazy val benchmark = project
  .in(file("benchmark"))
  .settings(name := s"$projectName-benchmark")
  .settings(
    Jmh / sourceDirectory     := (Test / sourceDirectory).value,
    Jmh / classDirectory      := (Test / classDirectory).value,
    Jmh / dependencyClasspath := (Test / dependencyClasspath).value,
    // rewire tasks, so that 'jmh:run' automatically invokes 'jmh:compile' (otherwise a clean 'jmh:run' would fail)
    Jmh / compile                           := (Jmh / compile).dependsOn(Test / compile).value,
    Jmh / run                               := (Jmh / run).dependsOn(Jmh / compile).evaluated,
    libraryDependencies += "org.openjdk.jmh" % "jmh-generator-annprocess" % "1.21" % Test
  )
  .dependsOn(macros.jvm(scala213) % "test->test")
  .dependsOn(coreShared.jvm(scala213))
  .enablePlugins(JmhPlugin)

//////////////////////////////////////////////////////
////////////////////// UTILS /////////////////////////
//////////////////////////////////////////////////////
def license: Project => Project =
  _.settings(
    startYear     := Some(2017),
    headerLicense := Some(HeaderLicense.ALv2("2021", Owner.name))
  ).enablePlugins(AutomateHeaderPlugin)

def defaultSettingsWithIt: Project => Project =
  _.settings(Defaults.itSettings)
    .configs(IntegrationTest)

def exampleModule(
    moduleName: String,
    moduleDir: String,
    dependsOn: sbt.ClasspathDep[sbt.ProjectReference]*
): Project =
  Project(
    s"$projectName-$moduleName",
    file(s"examples/$moduleDir")
  ).dependsOn(dependsOn: _*)
