package com.github.fsanaulla.macros

import com.github.fsanaulla.core.model.InfluxReader
import com.github.fsanaulla.core.test.FlatSpecWithMatchers
import jawn.ast._

class MacroReaderSpec extends FlatSpecWithMatchers {
  case class Test(name: String, age: Int, grade: Option[Double])
  val rd: InfluxReader[Test] = Macros.reader[Test]

  "Macros.reader" should "read with None" in {
    rd.read(JArray(Array(JNum(234324), JNum(4), JNull, JString("Fz")))) shouldEqual Test("Fz", 4, None)
  }

  it should "read with Some" in {
    rd.read(JArray(Array(JNum(234324), JNum(4), JNum(2.0), JString("Fz")))) shouldEqual Test("Fz", 4, Some(2.0))
  }
}
