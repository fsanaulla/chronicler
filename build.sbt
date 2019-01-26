import sbt.Keys.{libraryDependencies, name}

lazy val chronicler = project
  .in(file("."))
  .settings(parallelExecution in Compile := false)
  .aggregate(
    coreShared, coreIO, coreManagement,
    akkaShared, akkaIO, akkaManagement,
    ahcShared,  ahcIO,  ahcManagement,
    urlShared,  urlIO,  urlManagement,
    udp,
    macros
  )

//////////////////////////////////////////////////////
//////////////////// CORE MODULES ////////////////////
//////////////////////////////////////////////////////
lazy val coreIO = project
  .in(file("modules/core/io"))
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
  .in(file("modules/core/management"))
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
  .in(file("modules/core/shared"))
  .settings(Settings.propertyTestSettings: _*)
  .configs(Settings.PropertyTest)
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
  .in(file("modules/url/management"))
  .configs(Settings.CompileTimeIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(name := "chronicler-url-management")
  .dependsOn(coreManagement, urlShared)
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val urlIO = project
  .in(file("modules/url/io"))
  .configs(Settings.CompileTimeIntegrationTest)
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
  .in(file("modules/url/shared"))
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
  .in(file("modules/akka/management"))
  .configs(Settings.CompileTimeIntegrationTest)
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
  .in(file("modules/akka/io"))
  .configs(Settings.CompileTimeIntegrationTest)
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
  .in(file("modules/akka/shared"))
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
lazy val ahcManagement = project
  .in(file("modules/ahc/management"))
  .configs(Settings.CompileTimeIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(name := "chronicler-ahc-management")
  .dependsOn(coreManagement, ahcShared)
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val ahcIO = project
  .in(file("modules/ahc/io"))
  .configs(Settings.CompileTimeIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(name := "chronicler-ahc-io")
  .dependsOn(coreIO, ahcShared)
  .dependsOn(ahcManagement % "test->test")
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val ahcShared = project
  .in(file("modules/ahc/shared"))
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-ahc-shared",
    libraryDependencies ++= Dependencies.asyncDeps
  )
  .dependsOn(coreShared)
  .dependsOn(unitTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

//////////////////////////////////////////////////////
///////////////////// UPD MODULE /////////////////////
//////////////////////////////////////////////////////
lazy val udp = project
  .in(file("modules/udp"))
  .configs(Settings.CompileTimeIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .settings(
    name := "chronicler-udp",
    libraryDependencies ++= Dependencies.udpDep
  )
  .dependsOn(coreShared)
  .dependsOn(itTesting, urlIO, urlManagement % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

//////////////////////////////////////////////////////
///////////////////// MACRO MODULE ///////////////////
//////////////////////////////////////////////////////
lazy val macros = project
  .in(file("modules/macros"))
  .settings(Settings.propertyTestSettings: _*)
  .configs(Settings.PropertyTest)
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
  .in(file("modules/testing/it"))
  .settings(Settings.common: _*)
  .settings(
    name := "chronicler-it-testing",
    libraryDependencies ++= Dependencies.testingDeps
  )
  .dependsOn(coreShared)
  .dependsOn(unitTesting)

lazy val unitTesting = project
  .in(file("modules/testing/unit"))
  .settings(Settings.common: _*)
  .settings(
    name := "chronicler-unit-testing",
    libraryDependencies += Dependencies.scalaTest
  )
  .dependsOn(coreShared)

//////////////////////////////////////////////////////
////////////////////// EXAMPLES //////////////////////
//////////////////////////////////////////////////////
lazy val akkaIOExample = project.in(file("examples/akka/io"))