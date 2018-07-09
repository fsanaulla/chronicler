import sbt.{IntegrationTest, Provided, Test}

object Scope {
  val test = Test
  val it = IntegrationTest
  val all = "test,it"
  val compileTimeOnly = Provided
}
