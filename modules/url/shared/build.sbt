name := "chronicler-url-shared"

libraryDependencies ++=
  Library.scalaTest % Test :: Library.requestScala(scalaVersion.value) :: Nil
