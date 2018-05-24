package com.github.fsanaulla.macros

import com.github.fsanaulla.core.model.InfluxReader
import com.github.fsanaulla.core.test.FlatSpecWithMatchers
import com.github.fsanaulla.macros.annotations.{field, tag}
import jawn.ast._

class MacroReaderSpec extends FlatSpecWithMatchers {

  case class Test(
                   @tag name: String,
                   @tag surname: Option[String],
                   @field age: Int)

  val rd: InfluxReader[Test] = Macros.reader[Test]

  "Macros.reader" should "read with None" in {
    rd.read(JArray(Array(JNum(234324), JNum(4), JString("Fz"), JNull))) shouldEqual Test("Fz", None, 4)
  }

  it should "read with Some" in {
    rd.read(JArray(Array(JNum(234324), JNum(4), JString("Fz"), JString("Sr")))) shouldEqual Test("Fz", Some("Sr"), 4)
  }
}
