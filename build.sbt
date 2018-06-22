import sbt.Keys.{crossScalaVersions, libraryDependencies, name, organization, publishArtifact}
import sbt.url

lazy val commonSettings = Seq(
  scalaVersion := "2.12.6",
  organization := "com.github.fsanaulla",
  scalacOptions ++= Scalac.options,
  crossScalaVersions := Seq("2.11.8", scalaVersion.value),
  homepage := Some(url("https://github.com/fsanaulla/chronicler")),
  licenses += "Apache-2.0" -> url("https://opensource.org/licenses/Apache-2.0"),
  developers += Developer(id = "fsanaulla", name = "Faiaz Sanaulla", email = "fayaz.sanaulla@gmail.com", url = url("https://github.com/fsanaulla")),
  parallelExecution in IntegrationTest := false
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
  .settings(commonSettings: _*)
  .settings(publishArtifact := false)
  .aggregate(
    core,
    macros,
    urlHttp,
    akkaHttp,
    asyncHttp
//    udp
  )

lazy val core = project
  .in(file("core"))
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-core",
    scalacOptions ++= Seq(
      "-language:implicitConversions",
      "-language:postfixOps",
      "-language:higherKinds"
    ),
    libraryDependencies ++= Dependencies.coreDep
  )

lazy val urlHttp = project
  .in(file("url-http"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-url-http",
    libraryDependencies += Dependencies.urlHttp
  )
  .dependsOn(core % "compile->compile;test->test")
  .dependsOn(unitTesting % "test->test")
  .dependsOn(itTesting, unitTesting % "it->test")

lazy val akkaHttp = project
  .in(file("akka-http"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-akka-http",
    scalacOptions ++= Seq(
      "-language:postfixOps",
      "-language:higherKinds"
    ),
    libraryDependencies ++= Dependencies.akkaDep
  )
  .dependsOn(core % "compile->compile;test->test")
  .dependsOn(unitTesting % "test->test")
  .dependsOn(itTesting, unitTesting % "it->test")

lazy val asyncHttp = project
  .in(file("async-http"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-async-http",
    scalacOptions ++= Seq(
      "-language:implicitConversions",
      "-language:higherKinds"
    ),
    libraryDependencies ++= Dependencies.asyncHttp
  )
  .dependsOn(core % "compile->compile;test->test")
  .dependsOn(unitTesting % "test->test")
  .dependsOn(itTesting, unitTesting % "it->test")

lazy val udp = project
  .in(file("udp"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-udp",
    libraryDependencies += Dependencies.udpDep,
    test in Test := {}
  )
  .dependsOn(core, asyncHttp, macros, unitTesting % "it->test")

lazy val macros = project
  .in(file("macros"))
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "chronicler-macros",
    libraryDependencies ++= Dependencies.scalaReflect(scalaVersion.value) :: Nil
  )
  .dependsOn(core % "compile->compile;test->test")
  .dependsOn(unitTesting % "test->test")

lazy val itTesting = project
  .in(file("tests/it-testing"))
  .settings(commonSettings: _*)
  .settings(
    name := "chronicler-it-testing",
    libraryDependencies ++= Dependencies.itTestingDeps)
  .dependsOn(core, macros % "compile->compile")

lazy val unitTesting = project
  .in(file("tests/unit-testing"))
  .settings(commonSettings: _*)
  .settings(
    name := "chronicler-unit-testing",
    libraryDependencies += Dependencies.scalaTest)
  .dependsOn(core % "compile->compile")
