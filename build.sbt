import sbt.Keys.{crossScalaVersions, organization, publishArtifact, resolvers, version}
import sbt.url
import scoverage.ScoverageKeys.coverageMinimum

lazy val commonSettings = Seq(
  version := "0.3.4",
  organization := "com.github.fsanaulla",
  homepage := Some(url("https://github.com/fsanaulla/chronicler")),
  licenses += "MIT" -> url("https://opensource.org/licenses/MIT"),
  developers += Developer(id = "fsanaulla", name = "Faiaz Sanaulla", email = "fayaz.sanaulla@gmail.com", url = url("https://github.com/fsanaulla"))
)

lazy val root = (project in file("."))
  .settings(commonSettings:  _*)
  .aggregate(
    akkaHttp,
    asyncHttp,
    core
  )

lazy val akkaHttp = (project in file("akka-http"))
  .settings(commonSettings: _*)
  .settings(
    name := "chronicler-akka-http",
    scalaVersion := "2.12.4",
    crossScalaVersions := Seq(scalaVersion.value, "2.11.11"),
    libraryDependencies ++= Dependencies.akkaHttpDep
  ) dependsOn core

lazy val asyncHttp = (project in file("async-http"))
  .settings(commonSettings: _*)
  .settings(
    name := "chronicler-async-http",
    scalaVersion := "2.12.4",
    crossScalaVersions := Seq(scalaVersion.value, "2.11.11")
  ) dependsOn core

lazy val core = project
  .settings(commonSettings: _*)
  .settings(
    name := "chronicler-core",

    scalaVersion := "2.12.2",

    crossScalaVersions := Seq(scalaVersion.value, "2.11.11"),

    scalacOptions ++= Seq(
    "-feature",
    "-language:implicitConversions",
    "-language:postfixOps",
    "-Xplugin-require:macroparadise"),

    resolvers ++= Dependencies.projectResolvers,

    libraryDependencies ++= Dependencies.coreDep
  )

coverageMinimum := Coverage.min
coverageExcludedPackages := Coverage.exclude


// Publish section
useGpg := true

publishArtifact in Test := false

scmInfo := Some(
  ScmInfo(
    url("https://github.com/fsanaulla/chronicler"),
    "https://github.com/fsanaulla/chronicler.git"
  )
)
pomIncludeRepository := (_ => false)

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)


//credentials += Credentials(
//  "Sonatype Nexus Repository Manager",
//  "oss.sonatype.org",
//  sys.env.getOrElse("SONATYPE_LOGIN", ""),
//  sys.env.getOrElse("SONATYPE_PASS", "")
//)
