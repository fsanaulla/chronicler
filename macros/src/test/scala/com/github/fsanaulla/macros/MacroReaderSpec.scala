package com.github.fsanaulla.macros

import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.utils._
import org.scalatest.{FlatSpec, Ignore, Matchers}
import spray.json._

@Ignore
class MacroReaderSpec extends FlatSpec with Matchers {

  "Macros" should "generate reader" in {
    case class Test(name: String, age: Int)

    val rd = InfluxFormatter.reader[Test]

    rd.read(JsArray(JsNumber(234324), JsNumber(4), JsString("Fz"))) shouldEqual Test("Fz", 4)
  }
}
