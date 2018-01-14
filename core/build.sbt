name := "chronicler-core"

scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Xplugin-require:macroparadise")

resolvers ++= Dependencies.projectResolvers

libraryDependencies ++= Dependencies.coreDep