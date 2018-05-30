import sbt.Keys.{crossScalaVersions, libraryDependencies, name, organization, publishArtifact}
import sbt.url

val scalaVers = "2.12.6"

lazy val commonSettings = Seq(
  scalaVersion := scalaVers,
  organization := "com.github.fsanaulla",
  scalacOptions ++= Seq("-deprecation", "-feature"),
  crossScalaVersions := Seq("2.11.8", scalaVersion.value),
  homepage := Some(url("https://github.com/fsanaulla/chronicler")),
  licenses += "Apache-2.0" -> url("https://opensource.org/licenses/Apache-2.0"),
  developers += Developer(id = "fsanaulla", name = "Faiaz Sanaulla", email = "fayaz.sanaulla@gmail.com", url = url("https://github.com/fsanaulla")),
  parallelExecution := false
)

lazy val publishSettings = Seq(
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

lazy val chronicler = (project in file("."))
  .settings(publishArtifact := false)
  .aggregate(
    core,
    testing,
    macros,
    urlHttp,
    akkaHttp,
    asyncHttp,
    udp)

lazy val core = module(
  "core",
  "core",
  Dependencies.coreDep,
  "-language:implicitConversions" ::
  "-language:postfixOps" ::
  "-language:higherKinds" :: Nil
)

lazy val testing = module(
  "testing",
  "testing",
  Dependencies.testingDeps
).dependsOn(core % "compile->compile")

lazy val urlHttp = module(
  "urlHttp",
  "url-http",
  Dependencies.urlHttp :: Nil
).dependsOn(core % "compile->compile;test->test")
 .dependsOn(macros, testing % "test->test")

lazy val akkaHttp = module(
  "akkaHttp",
  "akka-http",
  Dependencies.akkaDep,
  "-language:postfixOps" :: "-language:higherKinds" :: Nil
).dependsOn(core % "compile->compile;test->test")
 .dependsOn(macros, testing % "test->test")

lazy val asyncHttp = module(
  "asyncHttp",
  "async-http",
  Dependencies.asyncHttp,
  "-language:implicitConversions" :: "-language:higherKinds" :: Nil
).dependsOn(core % "compile->compile;test->test")
 .dependsOn(macros, testing % "test->test")

lazy val udp = module(
  "udp",
  "udp",
  Dependencies.udpDep :: Nil
).dependsOn(core, asyncHttp, macros, testing % "test->test")

lazy val macros = module(
  "macros",
  "macros",
  Dependencies.scalaReflect(scalaVers) :: Nil
).dependsOn(core % "compile->compile;test->test")
 .dependsOn(testing % "test->test")

/**
  * Define chronicler module
  * @param sbtName   - sbt name, used in sbt shell sessions
  * @param dirName   - module location
  * @param deps      - module dependencies
  * @param scalaOpts - module scalac options
  * @return          - SBT project
  */
def module(sbtName: String,
           dirName: String,
           deps: Seq[sbt.ModuleID] = Nil,
           scalaOpts: Seq[String] = Nil): Project = {
  Project(id = sbtName, base = file(dirName))
    .settings(commonSettings: _*)
    .settings(publishSettings: _*)
    .settings(
      name := "chronicler-" + dirName,
      scalacOptions ++= scalaOpts,
      libraryDependencies ++= deps
    )
}
