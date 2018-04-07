package com.github.fsanaulla.macros

import com.github.fsanaulla.core.model.{InfluxFormatter, InfluxReader}
import com.github.fsanaulla.core.test.utils.FlatSpecWithMatchers
import com.github.fsanaulla.macros.annotations.{field, tag}
import jawn.ast.{JArray, JNum, JString}
import spray.json.{JsArray, JsNumber, JsString}

class MacroFormatterSpec extends FlatSpecWithMatchers {
  case class Test(@tag name: String, @field age: Int)
  val fm: InfluxFormatter[Test] = Macros.format[Test]

  "Macros.format" should "generate reader" in {
    fm.read(JArray(Array(JNum(234324), JNum(4), JString("Fz")))) shouldEqual Test("Fz", 4)
  }

  it should "generate writer" in {
    fm.write(Test("tName", 65)) shouldEqual "name=tName age=65"
  }
}
