name := "chronicler-core-shared"

scalacOptions ++= Seq(
  "-language:implicitConversions",
  "-language:higherKinds"
)

libraryDependencies ++= Library.coreDep ++ Library.coreTestDeps
