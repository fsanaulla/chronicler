name := "chronicler-macros"

libraryDependencies ++= Seq(
  "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0" % Test
) ++ Library.macroDeps(scalaVersion.value)
