import com.typesafe.sbt.SbtPgp.autoImportImpl.pgpReadOnly
import sbt.Keys.{resolvers, version}
import sbt.url
import scoverage.ScoverageKeys.coverageMinimum

name := "chronicler"

scalaVersion in ThisBuild := "2.12.2"

lazy val generalSettings = Seq(
  version := "0.3.3",
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

lazy val rootSettings = Seq(
  resolvers ++= Dependencies.projectResolvers,
  // Dependencies section
  libraryDependencies ++= Dependencies.rootDependencies,
  coverageMinimum := Coverage.min,
  coverageExcludedPackages := Coverage.exclude
)

lazy val macros = project.settings(macroSettings)

lazy val chronicler = (project in file("."))
  .settings(
    generalSettings,
    rootSettings,
    macroSettings
  )
  .dependsOn(macros)

inThisBuild(List(
  // These are normal sbt settings to configure for release, skip if already defined
  licenses += "MIT" -> url("https://opensource.org/licenses/MIT"),
  homepage := Some(url("https://github.com/fsanaulla/chronicler")),
  developers += Developer(id = "fsanaulla", name = "Faiaz Sanaulla", email = "fayaz.sanaulla@gmail.com", url = url("https://github.com/fsanaulla")),
  // These are the sbt-release-early settings to configure
  pgpPublicRing := file("/home/fayaz/.gnupg/pubring.gpg"),
  pgpSecretRing := file("/home/fayaz/.gnupg/pubring.gpg"),
  releaseEarlyWith := SonatypePublisher
))

useGpg := true

pgpReadOnly := false

publishArtifact in Test := false

publishMavenStyle := true

pomIncludeRepository := (_ => false)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/fsanaulla/chronicler"),
    "https://github.com/fsanaulla/chronicler.git")
)
//credentials += Credentials(
//  "Sonatype Nexus Repository Manager",
//  "oss.sonatype.org",
//  sys.env.getOrElse("SONATYPE_LOGIN", ""),
//  sys.env.getOrElse("SONATYPE_PASS", "")
//)
