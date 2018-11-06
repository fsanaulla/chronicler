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
lazy val urlManagement = project
  .in(file("url/management"))
  .configs(CompileTimeIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(name := "chronicler-url-management")
  .dependsOn(coreManagement, urlShared)
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val urlIO = project
  .in(file("url/io"))
  .configs(CompileTimeIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(name := "chronicler-url-io")
  .dependsOn(coreIO, urlShared)
  .dependsOn(urlManagement % "test->test")
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val urlShared = project
  .in(file("url/shared"))
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-url-shared",
    libraryDependencies += Dependencies.sttp
  )
  .dependsOn(coreShared)
  .dependsOn(unitTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

//////////////////////////////////////////////////////
////////////////// AKKA HTTP MODULES /////////////////
//////////////////////////////////////////////////////
lazy val akkaManagement = project
  .in(file("akka/management"))
  .configs(CompileTimeIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-akka-management",
    libraryDependencies += Dependencies.akkaTestKit % Scope.test
  )
  .dependsOn(coreManagement, akkaShared)
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val akkaIO = project
  .in(file("akka/io"))
  .configs(CompileTimeIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-akka-io",
    libraryDependencies += Dependencies.akkaTestKit % Scope.test
  )
  .dependsOn(coreIO, akkaShared)
  .dependsOn(akkaManagement % "test->test")
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val akkaShared = project
  .in(file("akka/shared"))
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-akka-shared",
    libraryDependencies ++= Dependencies.akkaDep
  )
  .dependsOn(coreShared)
  .dependsOn(unitTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

//////////////////////////////////////////////////////
///////////////// ASYNC HTTP MODULES /////////////////
//////////////////////////////////////////////////////
lazy val nettyManagement = project
  .in(file("netty/management"))
  .configs(CompileTimeIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(name := "chronicler-netty-management")
  .dependsOn(coreManagement, nettyShared)
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val nettyIO = project
  .in(file("netty/io"))
  .configs(CompileTimeIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(name := "chronicler-netty-io")
  .dependsOn(coreIO, nettyShared)
  .dependsOn(nettyManagement % "test->test")
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val nettyShared = project
  .in(file("netty/shared"))
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-netty-shared",
    libraryDependencies ++= Dependencies.asyncDeps
  )
  .dependsOn(coreShared)
  .dependsOn(unitTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)
//////////////////////////////////////////////////////
///////////////////// UPD MODULE /////////////////////
//////////////////////////////////////////////////////
lazy val udp = project
  .in(file("udp"))
  .configs(CompileTimeIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-udp",
    libraryDependencies ++= Dependencies.udpDep
  )
  .dependsOn(coreShared)
  .dependsOn(unitTesting)
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
    libraryDependencies ++= Dependencies.testingDeps
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