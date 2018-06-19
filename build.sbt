import sbt.Keys.{crossScalaVersions, libraryDependencies, name, organization, publishArtifact}
import sbt.url

lazy val commonSettings = Seq(
  scalaVersion := "2.12.6",
  organization := "com.github.fsanaulla",
  scalacOptions ++= Seq("-deprecation", "-feature"),
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
    testing,
    macros,
    urlHttp,
    akkaHttp,
    asyncHttp,
    udp
  )


lazy val core = module(
  "core",
  "core",
  Dependencies.coreDep,
  "-language:implicitConversions" ::
  "-language:postfixOps" ::
  "-language:higherKinds" :: Nil
)

lazy val testing = module(
  "testing",
  "testing",
  Dependencies.itTestingDeps
).dependsOn(core % "compile->compile")

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
  .dependsOn(itTesting % "it->test")

lazy val akkaHttp = module(
  "akkaHttp",
  "akka-http",
  Dependencies.akkaDep,
  "-language:postfixOps" :: "-language:higherKinds" :: Nil
).dependsOn(core % "compile->compile;test->test")
 .dependsOn(macros, testing % "test->test")

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
  .dependsOn(unitTesting % "compile->test")
  .dependsOn(itTesting % "compile->test")

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
  .dependsOn(unitTesting % "compile->test")

/**
  * Define chronicler module
  * @param sbtName   - sbt name, used in sbt shell sessions
  * @param dirName   - module location
  * @param deps      - module dependencies
  * @param scalaOpts - module scalac options
  * @return          - SBT project
  */
def module(sbtName: String,
           dirName: String,
           deps: Seq[sbt.ModuleID] = Nil,
           scalaOpts: Seq[String] = Nil): Project = {
  Project(id = sbtName, base = file(dirName))
    .settings(commonSettings: _*)
    .settings(publishSettings: _*)
    .settings(
      name := "chronicler-" + dirName,
      scalacOptions ++= scalaOpts,
      libraryDependencies ++= deps
    )
}

lazy val itTesting = project
  .in(file("testing/it"))
  .settings(libraryDependencies ++= Dependencies.itTestingDeps)
  .dependsOn(core, macros % "compile->compile")

lazy val unitTesting = project
  .in(file("testing/unit"))
  .settings(libraryDependencies += Dependencies.scalaTest)
  .dependsOn(core % "compile->compile")
