import sbt.Keys.{crossScalaVersions, organization, publishArtifact, resolvers, version}
import sbt.url
import scoverage.ScoverageKeys.coverageMinimum

lazy val commonSettings = Seq(
  version := "0.4.0",
  organization := "com.github.fsanaulla",
  homepage := Some(url("https://github.com/fsanaulla/chronicler")),
  licenses += "MIT" -> url("https://opensource.org/licenses/MIT"),
  developers += Developer(id = "fsanaulla", name = "Faiaz Sanaulla", email = "fayaz.sanaulla@gmail.com", url = url("https://github.com/fsanaulla"))
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

lazy val root = (project in file("."))
  .aggregate(core, akkaHttp, asyncHttp, udp)

lazy val core = project
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-core",
    scalaVersion := "2.12.4",
    crossScalaVersions := Seq(scalaVersion.value, "2.11.11"),
    scalacOptions ++= Seq(
      "-feature",
      "-language:implicitConversions",
      "-language:postfixOps"),
    resolvers ++= Dependencies.projectResolvers,
    libraryDependencies ++= Dependencies.coreDep
  )

lazy val akkaHttp = (project in file("akka-http"))
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-akka-http",
    scalaVersion := "2.12.4",
    crossScalaVersions := Seq(scalaVersion.value, "2.11.11"),
    libraryDependencies ++= Dependencies.akkaHttpDep,
    coverageMinimum := Coverage.min,
    coverageExcludedPackages := Coverage.exclude // todo: change
  ).dependsOn(core)

lazy val asyncHttp = (project in file("async-http"))
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-async-http",
    scalaVersion := "2.12.4",
    crossScalaVersions := Seq(scalaVersion.value, "2.11.11"),
    libraryDependencies ++= Dependencies.asyncHttpDep
  ).dependsOn(core)

lazy val udp = (project in file("udp-client"))
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-udp",
    scalaVersion := "2.12.4",
    crossScalaVersions := Seq(scalaVersion.value, "2.11.11"),
  ).dependsOn(core)

//credentials += Credentials(
//  "Sonatype Nexus Repository Manager",
//  "oss.sonatype.org",
//  sys.env.getOrElse("SONATYPE_LOGIN", ""),
//  sys.env.getOrElse("SONATYPE_PASS", "")
//)
