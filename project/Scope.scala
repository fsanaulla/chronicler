import sbt.{Provided, Test}

object Scope {
  val test = Test
  val it = Settings.LocalIntegrationTest
  val all = "test,it"
  val compileTimeOnly = Provided
}
