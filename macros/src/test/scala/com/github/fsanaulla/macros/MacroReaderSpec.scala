package com.github.fsanaulla.macros

import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.test.utils.FlatSpecWithMatchers
import com.github.fsanaulla.core.utils._
import jawn.ast.{JArray, JNum, JString}

class MacroReaderSpec extends FlatSpecWithMatchers {
  case class Test(name: String, age: Int)
  val rd: InfluxReader[Test] = Macros.reader[Test]

  "Macros.reader" should "generate reader" in {
    rd.read(JArray(Array(JNum(234324), JNum(4), JString("Fz")))) shouldEqual Test("Fz", 4)
  }
}
