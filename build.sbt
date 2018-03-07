import sbt.Keys.{crossScalaVersions, organization, publishArtifact, version}
import sbt.url

lazy val commonSettings = Seq(
  version := "0.4.0",
  organization := "com.github.fsanaulla",
  crossScalaVersions := Seq("2.12.4", "2.11.11"),
  homepage := Some(url("https://github.com/fsanaulla/chronicler")),
  licenses += "MIT" -> url("https://opensource.org/licenses/MIT"),
  developers += Developer(id = "fsanaulla", name = "Faiaz Sanaulla", email = "fayaz.sanaulla@gmail.com", url = url("https://github.com/fsanaulla")),
  parallelExecution := false
)

lazy val publishSettings = Seq(
  // Publish section
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
  .aggregate(
    core,
    akkaHttp,
    asyncHttp,
    udp,
    macros
  )

lazy val core = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-core",
    scalaVersion := "2.12.4",
    publishArtifact in (Test, packageBin) := true,
      scalacOptions ++= Seq(
      "-feature",
      "-language:implicitConversions",
      "-language:postfixOps"),
    libraryDependencies ++= Dependencies.coreDep)

lazy val akkaHttp = (project in file("akka-http"))
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-akka-http",
    scalaVersion := "2.12.4",
    libraryDependencies ++= Dependencies.akkaHttpDep
  ).dependsOn(core % "compile->compile;test->test")

lazy val asyncHttp = (project in file("async-http"))
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-async-http",
    scalaVersion := "2.12.4",
    libraryDependencies ++= Dependencies.asyncHttpDep
  ).dependsOn(core % "compile->compile;test->test")

lazy val udp = (project in file("udp"))
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-udp",
    scalaVersion := "2.12.4",
    libraryDependencies ++= Dependencies.udpDep)
  .dependsOn(core)
  .dependsOn(asyncHttp % "test->test")

lazy val macros = (project in file("macros"))
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-macros",
    scalaVersion := "2.12.4",
    scalacOptions ++= Seq("-deprecation", "-feature", "-print"),
    libraryDependencies ++= Dependencies.macrosDep
  ).dependsOn(core)

//credentials += Credentials(
//  "Sonatype Nexus Repository Manager",
//  "oss.sonatype.org",
//  sys.env.getOrElse("SONATYPE_LOGIN", ""),
//  sys.env.getOrElse("SONATYPE_PASS", "")
//)
