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
  .configure(defaultSettings)
  .dependsOn(coreShared)
  .enablePlugins(AutomateHeaderPlugin)

lazy val coreManagement = project
  .in(file("modules/core/management"))
  .settings(
    name := s"$projectName-core-management",
    scalacOptions += "-language:higherKinds"
  )
  .configure(defaultSettings)
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
  .configure(defaultSettings)
  .enablePlugins(AutomateHeaderPlugin)

//////////////////////////////////////////////////////
////////////////// URL HTTP MODULES //////////////////
//////////////////////////////////////////////////////
lazy val urlManagement = project
  .in(file("modules/url/management"))
  .settings(name := s"$projectName-url-management")
  .configure(defaultSettingsWithIt)
  .dependsOn(coreManagement, urlShared)
  .dependsOn(itTesting % "test->test")

lazy val urlIO = project
  .in(file("modules/url/io"))
  .settings(name := s"$projectName-url-io")
  .configure(defaultSettingsWithIt)
  .dependsOn(coreIO, urlShared)
  .dependsOn(urlManagement % "test->test")
  .dependsOn(itTesting % "test->test")

lazy val urlShared = project
  .in(file("modules/url/shared"))
  .settings(
    name := s"$projectName-url-shared",
    libraryDependencies ++= 
      Dependencies.scalaTest :: Dependencies.sttp :: Nil
  )
  .configure(defaultSettings)
  .dependsOn(coreShared)

//////////////////////////////////////////////////////
////////////////// AKKA HTTP MODULES /////////////////
//////////////////////////////////////////////////////
lazy val akkaManagement = project
  .in(file("modules/akka/management"))
  .settings(
    name := s"$projectName-akka-management",
    libraryDependencies += Dependencies.akkaTestKit % Scope.test
  )
  .configure(defaultSettingsWithIt)
  .dependsOn(coreManagement, akkaShared)
  .dependsOn(itTesting % "test->test")

lazy val akkaIO = project
  .in(file("modules/akka/io"))
  .settings(
    name := s"$projectName-akka-io",
    libraryDependencies += Dependencies.akkaTestKit % Scope.test
  )
  .configure(defaultSettingsWithIt)
  .dependsOn(coreIO, akkaShared)
  .dependsOn(akkaManagement % "test->test")
  .dependsOn(itTesting % "test->test")

lazy val akkaShared = project
  .in(file("modules/akka/shared"))
  .settings(
    name := s"$projectName-akka-shared",
    libraryDependencies ++=
      Dependencies.scalaTest :: Dependencies.akkaDep
  )
  .configure(defaultSettings)
  .dependsOn(coreShared)

//////////////////////////////////////////////////////
///////////////// ASYNC HTTP MODULES /////////////////
//////////////////////////////////////////////////////
lazy val ahcManagement = project
  .in(file("modules/ahc/management"))
  .settings(name := s"$projectName-ahc-management")
  .configure(defaultSettingsWithIt)
  .dependsOn(coreManagement, ahcShared)
  .dependsOn(itTesting % "test->test")

lazy val ahcIO = project
  .in(file("modules/ahc/io"))
  .settings(name := s"$projectName-ahc-io")
  .configure(defaultSettingsWithIt)
  .dependsOn(coreIO, ahcShared)
  .dependsOn(ahcManagement % "test->test")
  .dependsOn(itTesting % "test->test")

lazy val ahcShared = project
  .in(file("modules/ahc/shared"))
  .settings(
    name := s"$projectName-ahc-shared",
    libraryDependencies ++=
      Dependencies.scalaTest :: Dependencies.asyncDeps
  )
  .configure(defaultSettings)
  .dependsOn(coreShared)

//////////////////////////////////////////////////////
///////////////////// UPD MODULE /////////////////////
//////////////////////////////////////////////////////
lazy val udp = project
  .in(file("modules/udp"))
  .settings(name := s"$projectName-udp")
  .configure(defaultSettingsWithIt)
  .dependsOn(coreShared)
  .dependsOn(itTesting, urlIO, urlManagement % "test->test")

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
  .configure(defaultSettings)
  .dependsOn(coreShared)

//////////////////////////////////////////////////////
/////////////////// TESTING MODULES //////////////////
//////////////////////////////////////////////////////
lazy val itTesting = project
  .in(file("modules/testing"))
  .settings(Settings.common: _*)
  .settings(
    name := s"$projectName-testing",
    libraryDependencies ++= Dependencies.testingDeps
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

lazy val udpExample =
  exampleModule("udp-example", "udp", udp, macros)

//////////////////////////////////////////////////////
///////////////////// BENCHMARKS /////////////////////
//////////////////////////////////////////////////////
lazy val benchmark = project
  .in(file("benchmark"))
  .settings(Settings.common: _*)
  .settings(name := "chronicler-benchmark")
  .settings(
    sourceDirectory in Jmh := (sourceDirectory in Test).value,
    classDirectory in Jmh := (classDirectory in Test).value,
    dependencyClasspath in Jmh := (dependencyClasspath in Test).value,
      // rewire tasks, so that 'jmh:run' automatically invokes 'jmh:compile' (otherwise a clean 'jmh:run' would fail)
    compile in Jmh := (compile in Jmh).dependsOn(compile in Test).value,
    run in Jmh := (run in Jmh).dependsOn(Keys.compile in Jmh).evaluated,
    libraryDependencies += "org.openjdk.jmh" % "jmh-generator-annprocess" % "1.21" % Test
  )
  .dependsOn(itTesting, ahcIO, macros % "test->test")
  .enablePlugins(JmhPlugin)

//////////////////////////////////////////////////////
////////////////////// UTILS /////////////////////////
//////////////////////////////////////////////////////
def defaultSettings: Project => Project = _
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(Settings.header)
  .enablePlugins(AutomateHeaderPlugin)

def defaultSettingsWithIt: Project => Project = _
  .configs(Settings.CompileTimeIntegrationTest)
  .settings(Defaults.itSettings)
  .configure(defaultSettings)

def exampleModule(moduleName: String,
                  moduleDir: String,
                  dependsOn: sbt.ClasspathDep[sbt.ProjectReference]*): Project =
  Project(s"$projectName-$moduleName", file(s"examples/$moduleDir"))
    .settings(Settings.common: _*)
    .dependsOn(dependsOn: _*)