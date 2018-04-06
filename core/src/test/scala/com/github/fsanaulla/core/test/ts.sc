import com.github.fsanaulla.core.model.InfluxReader
import jawn.ast.{JParser, JValue}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

implicit val rd: InfluxReader[Test] = (js: JValue) => {
  Test(
    js.get("name").asString,
    js.get("age").asInt)
}

case class Test(name: String, age: Int)
val str = """
  {"name" : "f", "age": 5 }

""".stripMargin

val p = JParser

val t = Future.fromTry(p.parseFromString(str)).map(rd.read)

Await.result(t, 1.second)
