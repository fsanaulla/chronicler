import sbt.Keys.{publishArtifact, resolvers, version}
import sbt.url
import scoverage.ScoverageKeys.coverageMinimum

name := "chronicler"
version := "0.3.4"
organization := "com.github.fsanaulla"
homepage := Some(url("https://github.com/fsanaulla/chronicler"))
licenses += "MIT" -> url("https://opensource.org/licenses/MIT")


// used 2.12.2 instead of last one, because of macros paradise plugin supported version
scalaVersion in ThisBuild := "2.12.2"
crossScalaVersions in ThisBuild := Seq(scalaVersion.value, "2.11.11")
scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Xplugin-require:macroparadise")

// Dependencies section
resolvers ++= Dependencies.projectResolvers
libraryDependencies ++= Dependencies.rootDependencies

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
