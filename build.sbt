import Settings._
import sbt.Keys.{libraryDependencies, name}

//////////////////////////////////////////////////////
//////////////////// CORE MODULES ////////////////////
//////////////////////////////////////////////////////
lazy val coreIO = project
  .in(file("core/io"))
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header: _*)
  .settings(
    name := "chronicler-core-io",
    scalacOptions += "-language:higherKinds"
  )
  .dependsOn(coreShared)
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
  .dependsOn(coreShared)
  .enablePlugins(AutomateHeaderPlugin)

lazy val coreShared = project
  .in(file("core/shared"))
  .settings(propertyTestSettings: _*)
  .configs(PropertyTest)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-core-shared",
    libraryDependencies ++= Dependencies.coreDep,
    scalacOptions ++= Seq(
      "-language:implicitConversions",
      "-language:higherKinds"
    )
  )
  .enablePlugins(AutomateHeaderPlugin)

//////////////////////////////////////////////////////
////////////////// URL HTTP MODULES //////////////////
//////////////////////////////////////////////////////
lazy val urlHttpManagement = project
  .in(file("url-http/management"))
  .configs(LocalIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(name := "chronicler-url-http-management")
  .dependsOn(coreManagement, urlHttpShared)
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val urlHttpIO = project
  .in(file("url-http/io"))
  .configs(LocalIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(name := "chronicler-url-http-io")
  .dependsOn(coreIO, urlHttpShared)
  .dependsOn(urlHttpManagement % "test->test")
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val urlHttpShared = project
  .in(file("url-http/shared"))
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-url-http-shared",
    libraryDependencies += Dependencies.sttp
  )
  .dependsOn(coreShared)
  .dependsOn(unitTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

//////////////////////////////////////////////////////
////////////////// AKKA HTTP MODULES /////////////////
//////////////////////////////////////////////////////
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

//////////////////////////////////////////////////////
///////////////// ASYNC HTTP MODULES /////////////////
//////////////////////////////////////////////////////
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

//////////////////////////////////////////////////////
///////////////////// UPD MODULE /////////////////////
//////////////////////////////////////////////////////
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
  .dependsOn(coreShared)
  .enablePlugins(AutomateHeaderPlugin)

//////////////////////////////////////////////////////
///////////////////// MACRO MODULE ///////////////////
//////////////////////////////////////////////////////
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
  .dependsOn(coreShared)
  .enablePlugins(AutomateHeaderPlugin)

//////////////////////////////////////////////////////
/////////////////// TESTING MODULES //////////////////
//////////////////////////////////////////////////////
lazy val itTesting = project
  .in(file("testing/it"))
  .settings(Settings.common: _*)
  .settings(
    name := "chronicler-it-testing",
    libraryDependencies ++= Dependencies.itTestingDeps
  )
  .dependsOn(coreShared)
  .dependsOn(unitTesting)

lazy val unitTesting = project
  .in(file("testing/unit"))
  .settings(Settings.common: _*)
  .settings(
    name := "chronicler-unit-testing",
    libraryDependencies += Dependencies.scalaTest
  )
  .dependsOn(coreShared)