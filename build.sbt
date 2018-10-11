import Settings._
import sbt.Keys.{libraryDependencies, name}

// CORE
lazy val coreIO = project
  .in(file("core/io"))
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header: _*)
  .settings(
    name := "chronicler-core-io",
    scalacOptions += "-language:higherKinds"
  )
  .dependsOn(coreModel)
  .enablePlugins(AutomateHeaderPlugin)

lazy val coreManagement = project
  .in(file("core/management"))
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header: _*)
  .settings(
    name := "chronicler-core-management",
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

// CHRONICLER URL HTTP
lazy val urlHttpManagement = project
  .in(file("url-http/management"))
  .configs(LocalIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-url-http-management",
    libraryDependencies ++= Dependencies.httpClientTesting
  )
  .dependsOn(coreManagement, urlHttpShared)
  .enablePlugins(AutomateHeaderPlugin)

lazy val urlHttpIO = project
  .in(file("url-http/io"))
  .configs(LocalIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-url-http-io",
    libraryDependencies ++= Dependencies.httpClientTesting
  )
  .dependsOn(coreIO, urlHttpShared)
  .dependsOn(urlHttpManagement % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val urlHttpShared = project
  .in(file("url-http/shared"))
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-url-http-shared",
    libraryDependencies ++= Dependencies.urlHttp
  )
  .dependsOn(coreModel)
  .enablePlugins(AutomateHeaderPlugin)

// CHRONICLER AKKA HTTP
lazy val akkaHttp = project
  .in(file("akka-http"))
  .configs(LocalIntegrationTest)
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
  .dependsOn(coreIO)
  .enablePlugins(AutomateHeaderPlugin)

lazy val asyncHttp = project
  .in(file("async-http"))
  .configs(LocalIntegrationTest)
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
  .dependsOn(coreIO)
  .enablePlugins(AutomateHeaderPlugin)

lazy val udp = project
  .in(file("udp"))
  .configs(LocalIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-udp",
    libraryDependencies ++= Dependencies.udpDep,
    test in Scope.test := {}
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