package com.github.fsanaulla.macros

import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.test.utils.FlatSpecWithMatchers
import com.github.fsanaulla.core.utils._
import spray.json._

class MacroReaderSpec extends FlatSpecWithMatchers {
  case class Test(name: String, age: Int)
  val rd: InfluxReader[Test] = Macros.reader[Test]

  "Macros.reader" should "generate reader" in {
    rd.read(JsArray(JsNumber(234324), JsNumber(4), JsString("Fz"))) shouldEqual Test("Fz", 4)
  }
}
