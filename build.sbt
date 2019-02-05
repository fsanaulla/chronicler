import sbt.Keys.{libraryDependencies, name}

val projectName = "chronicler"
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
  .settings(
    name := s"$projectName-core-io",
    scalacOptions += "-language:higherKinds"
  )
  .configure(withDefaultSettings)
  .dependsOn(coreShared)
  .enablePlugins(AutomateHeaderPlugin)

lazy val coreManagement = project
  .in(file("modules/core/management"))
  .settings(
    name := s"$projectName-core-management",
    scalacOptions += "-language:higherKinds"
  )
  .configure(withDefaultSettings)
  .dependsOn(coreShared)
  .enablePlugins(AutomateHeaderPlugin)

lazy val coreShared = project
  .in(file("modules/core/shared"))
  .settings(Settings.propertyTestSettings: _*)
  .configs(Settings.PropertyTest)
  .settings(
    name := s"$projectName-core-shared",
    libraryDependencies ++= Dependencies.coreDep,
    scalacOptions ++= Seq(
      "-language:implicitConversions",
      "-language:higherKinds"
    )
  )
  .configure(withDefaultSettings)
  .enablePlugins(AutomateHeaderPlugin)

//////////////////////////////////////////////////////
////////////////// URL HTTP MODULES //////////////////
//////////////////////////////////////////////////////
lazy val urlManagement = project
  .in(file("modules/url/management"))
  .configs(Settings.CompileTimeIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(name := s"$projectName-url-management")
  .configure(withDefaultSettings)
  .dependsOn(coreManagement, urlShared)
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val urlIO = project
  .in(file("modules/url/io"))
  .configs(Settings.CompileTimeIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(name := s"$projectName-url-io")
  .configure(withDefaultSettings)
  .dependsOn(coreIO, urlShared)
  .dependsOn(urlManagement % "test->test")
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val urlShared = project
  .in(file("modules/url/shared"))
  .settings(
    name := s"$projectName-url-shared",
    libraryDependencies += Dependencies.sttp
  )
  .configure(withDefaultSettings)
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
  .settings(
    name := s"$projectName-akka-management",
    libraryDependencies += Dependencies.akkaTestKit % Scope.test
  )
  .configure(withDefaultSettings)
  .dependsOn(coreManagement, akkaShared)
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val akkaIO = project
  .in(file("modules/akka/io"))
  .configs(Settings.CompileTimeIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(
    name := s"$projectName-akka-io",
    libraryDependencies += Dependencies.akkaTestKit % Scope.test
  )
  .configure(withDefaultSettings)
  .dependsOn(coreIO, akkaShared)
  .dependsOn(akkaManagement % "test->test")
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val akkaShared = project
  .in(file("modules/akka/shared"))
  .settings(
    name := s"$projectName-akka-shared",
    libraryDependencies ++= Dependencies.akkaDep
  )
  .configure(withDefaultSettings)
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
  .settings(name := s"$projectName-ahc-management")
  .configure(withDefaultSettings)
  .dependsOn(coreManagement, ahcShared)
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val ahcIO = project
  .in(file("modules/ahc/io"))
  .configs(Settings.CompileTimeIntegrationTest)
  .settings(Defaults.itSettings)
  .settings(name := s"$projectName-ahc-io")
  .configure(withDefaultSettings)
  .dependsOn(coreIO, ahcShared)
  .dependsOn(ahcManagement % "test->test")
  .dependsOn(itTesting % "test->test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val ahcShared = project
  .in(file("modules/ahc/shared"))
  .settings(
    name := s"$projectName-ahc-shared",
    libraryDependencies ++= Dependencies.asyncDeps
  )
  .configure(withDefaultSettings)
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
  .settings(name := s"$projectName-udp")
  .configure(withDefaultSettings)
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
  .settings(
    name := s"$projectName-macros",
    libraryDependencies ++= Dependencies.macroDeps(scalaVersion.value)
  )
  .configure(withDefaultSettings)
  .dependsOn(coreShared)
  .enablePlugins(AutomateHeaderPlugin)

//////////////////////////////////////////////////////
/////////////////// TESTING MODULES //////////////////
//////////////////////////////////////////////////////
lazy val itTesting = project
  .in(file("modules/testing/it"))
  .settings(Settings.common: _*)
  .settings(
    name := s"$projectName-it-testing",
    libraryDependencies ++= Dependencies.testingDeps
  )
  .dependsOn(coreShared)
  .dependsOn(unitTesting)

lazy val unitTesting = project
  .in(file("modules/testing/unit"))
  .settings(Settings.common: _*)
  .settings(
    name := s"$projectName-unit-testing",
    libraryDependencies += Dependencies.scalaTest
  )
  .dependsOn(coreShared)

//////////////////////////////////////////////////////
////////////////////// EXAMPLES //////////////////////
//////////////////////////////////////////////////////
lazy val akkaIOExample =
  exampleModule("akka-io-example", "akka/io", akkaIO, macros)

lazy val akkaManagementExample =
  exampleModule("akka-management-example", "akka/management", akkaManagement)

lazy val ahcIOExample =
  exampleModule("ahc-io-example", "ahc/io", ahcIO, macros)

lazy val ahcManagementExample =
  exampleModule("ahc-management-example", "ahc/management", ahcManagement)

lazy val urlIOExample =
  exampleModule("url-io-example", "url/io", urlIO, macros)

lazy val urlManagementExample =
  exampleModule("url-management-example", "url/management", urlManagement)

//////////////////////////////////////////////////////
////////////////////// UTILS /////////////////////////
//////////////////////////////////////////////////////
def withDefaultSettings: Project => Project = _
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)

def exampleModule(moduleName: String,
                  moduleDir: String,
                  dependsOn: sbt.ClasspathDep[sbt.ProjectReference]*): Project =
  Project(s"$projectName-$moduleName", file(s"examples/$moduleDir"))
    .settings(Settings.common: _*)
    .dependsOn(dependsOn: _*)