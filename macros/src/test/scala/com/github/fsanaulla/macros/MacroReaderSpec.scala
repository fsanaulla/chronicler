package com.github.fsanaulla.macros

import com.github.fsanaulla.chronicler.testing.FlatSpecWithMatchers
import com.github.fsanaulla.core.model.InfluxReader
import com.github.fsanaulla.macros.annotations.{field, tag, timestamp}
import jawn.ast._

class MacroReaderSpec extends FlatSpecWithMatchers {

  case class Test(@tag name: String,
                  @tag surname: Option[String],
                  @field age: Int)

  case class Test1(@tag name: String,
                   @tag surname: Option[String],
                   @field age: Int,
                   @timestamp time: Long)

  val rd: InfluxReader[Test] = Macros.reader[Test]
  val rd1: InfluxReader[Test1] = Macros.reader[Test1]

  "Macros.reader" should "read with None and ignore time" in {
    rd.read(JArray(Array(JString("2015-08-04T19:05:14.318570484Z"), JNum(4), JString("Fz"), JNull))) shouldEqual Test("Fz", None, 4)
  }

  it should "read with Some and ignore time" in {
    rd.read(JArray(Array(JString("2015-08-04T19:05:14Z"), JNum(4), JString("Fz"), JString("Sr")))) shouldEqual Test("Fz", Some("Sr"), 4)
  }

  it should "read with timestamp" in {
    rd1.read(JArray(Array(JString("2015-08-04T19:05:14.318570484Z"), JNum(4), JString("Fz"), JNull))) shouldEqual Test1("Fz", None, 4, 1438715114318570484L)
  }
}
