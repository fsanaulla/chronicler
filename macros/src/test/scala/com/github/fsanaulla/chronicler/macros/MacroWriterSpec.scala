package com.github.fsanaulla.chronicler.macros

import com.github.fsanaulla.chronicler.core.model.InfluxWriter
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag, timestamp}
import com.github.fsanaulla.chronicler.testing.FlatSpecWithMatchers

class MacroWriterSpec extends FlatSpecWithMatchers {

  case class Test(@tag name: String,
                  @tag surname: Option[String],
                  @field school: String,
                  @field age: Int)

  case class Test1(@tag name: String,
                   @tag surname: Option[String],
                   @field age: Int,
                   @field school: String,
                   @timestamp time: Long)

  val wr: InfluxWriter[Test] = Macros.writer[Test]
  val wr1: InfluxWriter[Test1] = Macros.writer[Test1]

  "Macros.writer" should "write with None" in {
    wr.write(Test("nm", None, "Berkly", 65)) shouldEqual "name=nm school=\"Berkly\",age=65i"
  }

  it should "write with Some" in {
    wr.write(Test("nm", Some("sn"), "Berkly", 65)) shouldEqual "name=nm,surname=sn school=\"Berkly\",age=65i"
  }

  it should "write with timestamp" in {
    wr1.write(Test1("nm", Some("sn"), 65, "Berkly", 1438715114318570484L)) shouldEqual "name=nm,surname=sn age=65i,school=\"Berkly\" 1438715114318570484"
  }
}
