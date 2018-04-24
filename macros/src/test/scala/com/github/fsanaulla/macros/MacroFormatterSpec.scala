package com.github.fsanaulla.macros

import com.github.fsanaulla.core.model.InfluxFormatter
import com.github.fsanaulla.core.test.FlatSpecWithMatchers
import com.github.fsanaulla.macros.annotations.{field, tag}
import jawn.ast.{JArray, JNull, JNum, JString}

class MacroFormatterSpec extends FlatSpecWithMatchers {

  case class Test(@tag name: String, @tag surname: Option[String], @field age: Int)

  val fm: InfluxFormatter[Test] = Macros.format[Test]

  "Macros.format" should "read with None" in {
    fm.read(JArray(Array(JNum(234324), JNum(4), JString("Fz"), JNull))) shouldEqual Test("Fz", None, 4)
  }

  it should "read with Some" in {
    fm.read(JArray(Array(JNum(234324), JNum(4), JString("Fz"), JString("Sz")))) shouldEqual Test("Fz", Some("Sz"), 4)
  }

  it should "write with None" in {
    fm.write(Test("tName", None, 65)) shouldEqual "name=tName age=65"
  }

  it should "write with Some" in {
    fm.write(Test("tName", Some("Sz"), 65)) shouldEqual "name=tName,surname=Sz age=65"
  }
}
