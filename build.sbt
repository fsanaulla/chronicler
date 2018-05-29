import sbt.Keys.{crossScalaVersions, name, organization, publishArtifact}
import sbt.url

lazy val commonSettings = Seq(
  scalaVersion := "2.12.6",
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

lazy val core = project
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-core",
    scalacOptions ++= Seq(
      "-language:implicitConversions",
      "-language:postfixOps",
      "-language:higherKinds"),
    libraryDependencies ++= Dependencies.coreDep
  )

lazy val testing = project
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-testing",
    scalacOptions ++= Seq(
      "-language:implicitConversions",
      "-language:postfixOps",
      "-language:higherKinds"),
    libraryDependencies ++= Dependencies.testingDeps
  )
  .dependsOn(core % "compile->compile")

lazy val urlHttp = module(
  "url-http",
  "urlHttp",
  Dependencies.urlHttp :: Nil
)

lazy val akkaHttp = module(
  "akka-http",
  "akkaHttp",
  Dependencies.akkaDep,
  "-language:postfixOps" :: "-language:higherKinds" :: Nil
)

lazy val asyncHttp = module(
  "async-http",
  "asyncHttp",
  Dependencies.asyncHttp :: Nil,
  "-language:implicitConversions" :: "-language:higherKinds" :: Nil)

lazy val udp = project
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(name := "chronicler-udp")
  .dependsOn(core, asyncHttp, macros, testing % "test->test")

lazy val macros = project
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-macros",
    libraryDependencies += Dependencies.scalaReflect(scalaVersion.value)
  )
  .dependsOn(core % "compile->compile;test->test")
  .dependsOn(testing % "test->test")

def module(dir: String, name: String, deps: Seq[sbt.ModuleID] = Nil, scalaOpts: Seq[String] = Nil): Project = {
  Project(id = name, base = file(dir))
    .settings(commonSettings: _*)
    .settings(publishSettings: _*)
    .settings(
      scalacOptions ++= scalaOpts,
      libraryDependencies ++= deps
    )
    .dependsOn(core % "compile->compile;test->test")
    .dependsOn(macros, testing % "test->test")
}
