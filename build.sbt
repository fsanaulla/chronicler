import sbt.Keys.{resolvers, version}
import sbt.url
import scoverage.ScoverageKeys.coverageMinimum

name := "chronicler"

scalaVersion in ThisBuild := "2.12.2"
crossScalaVersions in ThisBuild := Seq(scalaVersion.value, "2.11.11")

lazy val generalSettings = Seq(
  version := "0.3",
  organization := "com.github.fsanaulla",
  scalacOptions ++= Seq(
    "-feature",
    "-language:implicitConversions",
    "-language:postfixOps")
)

lazy val macroSettings = Seq(
  libraryDependencies ++= Dependencies.macrosDependencies,
  addCompilerPlugin(Dependencies.paradise),
  scalacOptions += "-Xplugin-require:macroparadise"
)

lazy val chroniclerSettings = Seq(
  resolvers ++= Dependencies.projectResolvers,
  // Dependencies section
  libraryDependencies ++= Dependencies.rootDependencies,
  coverageMinimum := Coverage.min,
  coverageExcludedPackages := Coverage.exclude
)

lazy val macros = project.settings(generalSettings, macroSettings)

lazy val chronicler = (project in file("."))
  .settings(
    generalSettings,
    chroniclerSettings,
    macroSettings
  )
  .dependsOn(macros)

useGpg := true

publishArtifact in Test := false

scmInfo := Some(
  ScmInfo(
    url("https://github.com/fsanaulla/chronicler"),
    "https://github.com/fsanaulla/chronicler.git")
)

licenses += "MIT" -> url("https://opensource.org/licenses/MIT")

homepage := Some(url("https://github.com/fsanaulla/chronicler"))

developers += Developer(id = "fsanaulla", name = "Faiaz Sanaulla", email = "fayaz.sanaulla@gmail.com", url = url("https://github.com/fsanaulla"))

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
