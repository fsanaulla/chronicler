import sbt.Keys.{libraryDependencies, name}

val projectName = "chronicler"

lazy val chronicler = project
  .in(file("."))
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(parallelExecution in Compile := false)
//  .aggregate(
//    coreIO,
//    coreManagement,
//    coreShared,
//    ahcIO,
//    ahcManagement,
//    ahcShared,
//    akkaIO,
//    akkaManagement,
//    akkaShared,
//    urlIO,
//    urlManagement,
//    urlShared,
//    macros,
//    udp
//  )

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

lazy val coreManagement = project
  .in(file("modules/core/management"))
  .settings(
    name := s"$projectName-core-management",
    scalacOptions += "-language:higherKinds"
  )
  .configure(defaultSettings)
  .dependsOn(coreShared)

lazy val coreShared = project
  .in(file("modules/core/shared"))
  .settings(Settings.propertyTestSettings: _*)
  .configs(Settings.PropertyTest)
  .configure(defaultSettings)

//////////////////////////////////////////////////////
////////////////// URL HTTP MODULES //////////////////
//////////////////////////////////////////////////////
lazy val urlManagement = project
  .in(file("modules/url/management"))
  .settings(name := s"$projectName-url-management")
  .configure(defaultSettingsWithIt)
  .dependsOn(coreManagement, urlShared)
  .dependsOn(testing % "it,test")

lazy val urlIO = project
  .in(file("modules/url/io"))
  .settings(name := s"$projectName-url-io")
  .configure(defaultSettingsWithIt)
  .dependsOn(coreIO, urlShared)
  .dependsOn(urlManagement % "it,test")
  .dependsOn(macros % "it,test")
  .dependsOn(testing % "it,test")

lazy val urlShared = project
  .in(file("modules/url/shared"))
  .settings(
    name := s"$projectName-url-shared",
    libraryDependencies ++=
      Library.scalaTest % Test :: Library.requestScala(scalaVersion.value) :: Nil
  )
  .configure(defaultSettings)
  .dependsOn(coreShared)

//////////////////////////////////////////////////////
////////////////// AKKA HTTP MODULES /////////////////
//////////////////////////////////////////////////////
lazy val akkaManagement = project
  .in(file("modules/akka/management"))
  .configure(defaultSettingsWithIt)
  .dependsOn(coreManagement, akkaShared)
  .dependsOn(testing % "test,it")

lazy val akkaIO = project
  .in(file("modules/akka/io"))
  .configure(defaultSettingsWithIt)
  .dependsOn(coreIO, akkaShared)
  .dependsOn(akkaManagement % "test,it")
  .dependsOn(testing % "test,it")
  .dependsOn(macros % "test,it")

lazy val akkaShared = project
  .in(file("modules/akka/shared"))
  .settings(
    name := s"$projectName-akka-shared",
    libraryDependencies ++=
      Library.scalaTest % Test :: Library.akkaDep
  )
  .configure(defaultSettings)
  .dependsOn(coreShared)

//////////////////////////////////////////////////////
///////////////// ASYNC HTTP MODULES /////////////////
//////////////////////////////////////////////////////
lazy val ahcManagement = project
  .in(file("modules/ahc/management"))
  .configure(defaultSettingsWithIt)
  .dependsOn(coreManagement, ahcShared)
  .dependsOn(testing % "it,test")

lazy val ahcIO = project
  .in(file("modules/ahc/io"))
  .settings(name := s"$projectName-ahc-io")
  .configure(defaultSettingsWithIt)
  .dependsOn(coreIO, ahcShared)
  .dependsOn(ahcManagement % "it,test")
  .dependsOn(testing % "it,test")

lazy val ahcShared = project
  .in(file("modules/ahc/shared"))
  .settings(
    name := s"$projectName-ahc-shared",
    libraryDependencies ++=
      Library.scalaTest % Test :: Library.asyncDeps
  )
  .configure(defaultSettings)
  .dependsOn(coreShared)

//////////////////////////////////////////////////////
///////////////////// UPD MODULE /////////////////////
//////////////////////////////////////////////////////
lazy val udp = project
  .in(file("modules/udp"))
  .configure(defaultSettingsWithIt)
  .dependsOn(coreShared)
  .dependsOn(testing % "it,test")
  .dependsOn(urlIO % "it,test")
  .dependsOn(urlManagement % "it,test")

//////////////////////////////////////////////////////
///////////////////// MACRO MODULE ///////////////////
//////////////////////////////////////////////////////
lazy val macros = project
  .in(file("modules/macros"))
  .settings(Settings.propertyTestSettings: _*)
  .configs(Settings.PropertyTest)
  .configure(defaultSettings)
  .dependsOn(coreShared)

//////////////////////////////////////////////////////
/////////////////// TESTING MODULES //////////////////
//////////////////////////////////////////////////////
lazy val testing = project
  .in(file("modules/testing"))
  .settings(Settings.common: _*)
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
  .dependsOn(macros % "test->test")
  .dependsOn(coreShared)
  .enablePlugins(JmhPlugin)

//////////////////////////////////////////////////////
////////////////////// UTILS /////////////////////////
//////////////////////////////////////////////////////
def defaultSettings: Project => Project =
  _.settings(Settings.common: _*)
    .settings(Settings.publish: _*)
    .settings(Settings.header)
    .enablePlugins(AutomateHeaderPlugin)

def defaultSettingsWithIt: Project => Project =
  _.configs(Settings.IntegrationTest)
    .settings(Defaults.itSettings)
    .configure(defaultSettings)

def exampleModule(
    moduleName: String,
    moduleDir: String,
    dependsOn: sbt.ClasspathDep[sbt.ProjectReference]*
): Project =
  Project(s"$projectName-$moduleName", file(s"examples/$moduleDir"))
    .settings(Settings.common: _*)
    .dependsOn(dependsOn: _*)
