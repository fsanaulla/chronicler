name := "chronicler"

organization := "com.github.fsanaulla"

scalaVersion := "2.12.3"

crossScalaVersions := Seq(scalaVersion.value, "2.11.8")

scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps"
)

// Developer section
homepage := Some(url("https://github.com/fsanaulla/chronicler"))

licenses += "MIT" -> url("https://opensource.org/licenses/MIT")

scmInfo := Some(
  ScmInfo(
    url("https://github.com/fsanaulla/chronicler"),
    "https://github.com/fsanaulla/chronicler.git")
)

developers += Developer(
  id = "fsanaulla",
  name = "Faiaz Sanaulla",
  email = "fayaz.sanaulla@gmail.com",
  url = url("https://github.com/fsanaulla")
)

// Dependencies section
libraryDependencies ++= Dependencies.dep

// Coverage section
coverageMinimum := CoverageInfo.min
coverageExcludedPackages := CoverageInfo.exclude

// Publish section
useGpg := true

pgpReadOnly := false

releaseCrossBuild := true

publishArtifact in Test := false

publishMavenStyle := true

pomIncludeRepository := (_ => false)


import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _), enableCrossBuild = true),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _), enableCrossBuild = true),
  pushChanges
)