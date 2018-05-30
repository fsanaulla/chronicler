package com.github.fsanaulla.macros

import com.github.fsanaulla.chronicler.testing.FlatSpecWithMatchers
import com.github.fsanaulla.core.model.InfluxFormatter
import com.github.fsanaulla.macros.annotations.{field, tag, timestamp}
import jawn.ast.{JArray, JNull, JNum, JString}

class MacroFormatterSpec extends FlatSpecWithMatchers {

  case class Test(@tag name: String,
                  @tag surname: Option[String],
                  @field age: Int,
                  @timestamp time: Long)

  val fm: InfluxFormatter[Test] = Macros.format[Test]

  "Macros.format" should "read with None" in {
    fm
      .read(JArray(Array(JString("2015-08-04T19:05:14.318570484Z"), JNum(4), JString("Fz"), JNull)))
      .shouldEqual(Test("Fz", None, 4, 1438715114318570484L))
  }

  it should "read with Some" in {
    fm
      .read(JArray(Array(JString("2015-08-04T19:05:14.318570484Z"), JNum(4), JString("Fz"), JString("Sz"))))
      .shouldEqual(Test("Fz", Some("Sz"), 4, 1438715114318570484L))
  }

  it should "write with None" in {
    fm
      .write(Test("tName", None, 65, 1438715114318570484L))
      .shouldEqual("name=tName age=65i 1438715114318570484")
  }

  it should "write with Some" in {
    fm
      .write(Test("tName", Some("Sz"), 65, 1438715114318570484L))
      .shouldEqual("name=tName,surname=Sz age=65i 1438715114318570484")
  }
}
