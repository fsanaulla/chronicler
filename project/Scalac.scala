object Scalac {

  def options(version: String): Seq[String] = version match {
    case v if v.startsWith("2.12") => options12
    case _ => options11
  }

  // Warn if an implicit parameter is unused.
  private val unusedImplicits = "-Ywarn-unused:implicits"

  // scalac 2.11 options
  private val options11 = Seq(
    "-deprecation",
    "-feature",
    "-encoding", "utf-8",       // Specify character encoding used by source files.
    "-Ywarn-unused:imports",    // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals",     // Warn if a local definition is unused.
    "-Ywarn-unused:params",     // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars",    // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates"    // Warn if a private member is unused.
//    "-Xfatal-warnings"
  )

  // scalac 2.12 options
  private val options12: Seq[String] = unusedImplicits +: options11
}
