import sbt.Keys.{crossScalaVersions, organization, publishArtifact}
import sbt.url

lazy val commonSettings = Seq(
  scalaVersion := "2.12.4",
  organization := "com.github.fsanaulla",
  scalacOptions ++= Seq("-deprecation", "-feature", "-Ylog-classpath"),
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
    akkaHttp,
    asyncHttp,
    udp,
    macros
  )

lazy val core = project
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-core",
    publishArtifact in (Test, packageBin) := true,
      scalacOptions ++= Seq(
        "-language:implicitConversions",
        "-language:postfixOps"),
    libraryDependencies ++= Dependencies.coreDep
  )

lazy val akkaHttp = (project in file("akka-http"))
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-akka-http",
    scalacOptions += "-language:postfixOps",
    libraryDependencies ++= Dependencies.akkaHttpDep
  )
  .dependsOn(core % "compile->compile;test->test")
  .dependsOn(macros % "test->test")

lazy val asyncHttp = (project in file("async-http"))
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-async-http",
    libraryDependencies += Dependencies.asyncHttp
  ).dependsOn(core % "compile->compile;test->test")

lazy val udp = project
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(name := "chronicler-udp")
  .dependsOn(core)
  .dependsOn(asyncHttp % "test->test")
  .dependsOn(macros % "test->test")

lazy val macros = project
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-macros",
    scalacOptions ++= Seq("-deprecation", "-feature"),
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
  ).dependsOn(core % "compile->compile;test->test")

addCommandAlias("fullTest", ";clean;compile;test:compile;coverage;test;coverageReport")

addCommandAlias("fullRelease", ";clean;publishSigned;sonatypeRelease")

// build all project in one task, for combining coverage reports and decreasing CI jobs
addCommandAlias(
  "universeTest",
  ";project core;+fullTest;" +
  "project akkaHttp;+fullTest;" +
  "project asyncHttp;+fullTest;" +
  "project udp;+fullTest;" +
  "project macros;+fullTest"
)
