object Scalac {

  val options = Seq(
    "-deprecation",
    "-feature",
    "-encoding", "utf-8",       // Specify character encoding used by source files.
    "-Ywarn-unused:implicits",  // Warn if an implicit parameter is unused.
    "-Ywarn-unused:imports",    // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals",     // Warn if a local definition is unused.
    "-Ywarn-unused:params",     // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars",    // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates"    // Warn if a private member is unused.
  )
}
