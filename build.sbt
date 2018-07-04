import sbt.Keys.{libraryDependencies, name, publishArtifact}

lazy val chronicler = (project in file("."))
  .settings(Settings.common: _*)
  .settings(publishArtifact := false)
  .aggregate(
    core,
    macros,
    urlHttp,
    akkaHttp,
    asyncHttp,
    udp
  )

lazy val core = project
  .in(file("core"))
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
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
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(
    name := "chronicler-url-http",
    libraryDependencies ++= Dependencies.urlHttp
  )
  .dependsOn(core % "compile->compile;test->test")

lazy val akkaHttp = project
  .in(file("akka-http"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(
    name := "chronicler-akka-http",
    scalacOptions ++= Seq(
      "-language:postfixOps",
      "-language:higherKinds"
    ),
    libraryDependencies ++= Dependencies.akkaDep
  )
  .dependsOn(core % "compile->compile;test->test")

lazy val asyncHttp = project
  .in(file("async-http"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(
    name := "chronicler-async-http",
    scalacOptions ++= Seq(
      "-language:implicitConversions",
      "-language:higherKinds"
    ),
    libraryDependencies ++= Dependencies.asyncHttp
  )
  .dependsOn(core % "compile->compile;test->test")

lazy val udp = project
  .in(file("udp"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(
    name := "chronicler-udp",
    libraryDependencies ++= Dependencies.udpDep,
    test in Test := {}
  )
  .dependsOn(core % "compile->compile;test->test")

lazy val macros = project
  .in(file("macros"))
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(
    name := "chronicler-macros",
    libraryDependencies ++= Dependencies.macroDeps(scalaVersion.value)
  )
  .dependsOn(core % "compile->compile;test->test")

// Only for test purpose
lazy val itTesting = project
  .in(file("tests/it-testing"))
  .settings(Settings.common: _*)
  .settings(
    name := "chronicler-it-testing",
    libraryDependencies ++= Dependencies.itTestingDeps
  )
  .dependsOn(core % "compile->compile")

lazy val unitTesting = project
  .in(file("tests/unit-testing"))
  .settings(Settings.common: _*)
  .settings(
    name := "chronicler-unit-testing",
    libraryDependencies += Dependencies.scalaTest % Provided
  )
  .dependsOn(core % "compile->compile")