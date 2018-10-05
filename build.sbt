import sbt.Keys.{libraryDependencies, name}

lazy val PropertyTest = config("pt") extend Test
lazy val propertyTestSettings = inConfig(PropertyTest)(Defaults.testSettings)

lazy val coreApi = project
  .in(file("core/api"))
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header: _*)
  .settings(
    name := "chronicler-core-api",
    scalacOptions += "-language:higherKinds"
  )
  .dependsOn(coreModel)
  .enablePlugins(AutomateHeaderPlugin)

lazy val coreModel = project
  .in(file("core/model"))
  .settings(propertyTestSettings: _*)
  .configs(PropertyTest)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-core-model",
    libraryDependencies ++= Dependencies.coreDep,
    scalacOptions ++= Seq(
      "-language:implicitConversions",
      "-language:higherKinds"
    )
  )
  .enablePlugins(AutomateHeaderPlugin)

lazy val urlHttp = project
  .in(file("url-http"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-url-http",
    libraryDependencies ++= Dependencies.urlHttp
  )
  .dependsOn(coreApi)
  .enablePlugins(AutomateHeaderPlugin)

lazy val akkaHttp = project
  .in(file("akka-http"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-akka-http",
    scalacOptions ++= Seq(
      "-language:postfixOps",
      "-language:higherKinds"
    ),
    libraryDependencies ++= Dependencies.akkaDep
  )
  .dependsOn(coreApi)
  .enablePlugins(AutomateHeaderPlugin)

lazy val asyncHttp = project
  .in(file("async-http"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-async-http",
    scalacOptions ++= Seq(
      "-language:implicitConversions",
      "-language:higherKinds"
    ),
    libraryDependencies ++= Dependencies.asyncHttp
  )
  .dependsOn(coreApi)
  .enablePlugins(AutomateHeaderPlugin)

lazy val udp = project
  .in(file("udp"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-udp",
    libraryDependencies ++= Dependencies.udpDep,
    test in Test := {}
  )
  .dependsOn(coreModel)
  .enablePlugins(AutomateHeaderPlugin)

lazy val macros = project
  .in(file("macros"))
  .settings(propertyTestSettings: _*)
  .configs(PropertyTest)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-macros",
    libraryDependencies ++= Dependencies.macroDeps(scalaVersion.value)
  )
  .dependsOn(coreModel)
  .enablePlugins(AutomateHeaderPlugin)

// Only for test purpose
lazy val itTesting = project
  .in(file("tests/it-testing"))
  .settings(Settings.common: _*)
  .settings(
    name := "chronicler-it-testing",
    version := "0.1.0",
    libraryDependencies ++= Dependencies.itTestingDeps
  )
  .dependsOn(coreModel % "compile->compile")

lazy val unitTesting = project
  .in(file("tests/unit-testing"))
  .settings(Settings.common: _*)
  .settings(
    name := "chronicler-unit-testing",
    version := "0.1.0",
    libraryDependencies += Dependencies.scalaTest
  )
  .dependsOn(coreModel % "compile->compile")