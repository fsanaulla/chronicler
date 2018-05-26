package com.github.fsanaulla.macros

import com.github.fsanaulla.core.model.InfluxWriter
import com.github.fsanaulla.core.test.FlatSpecWithMatchers
import com.github.fsanaulla.macros.annotations.{field, tag, timestamp}

class MacroWriterSpec extends FlatSpecWithMatchers {

  case class Test(@tag name: String,
                  @tag surname: Option[String],
                  @field age: Int)

  case class Test1(@tag name: String,
                   @tag surname: Option[String],
                   @field age: Int,
                   @timestamp time: Long)

  val wr: InfluxWriter[Test] = Macros.writer[Test]
  val wr1: InfluxWriter[Test1] = Macros.writer[Test1]

  "Macros.writer" should "write with None" in {
    wr.write(Test("nm", None, 65)) shouldEqual "name=nm age=65"
  }

  it should "write with Some" in {
    wr.write(Test("nm", Some("sn"), 65)) shouldEqual "name=nm,surname=sn age=65"
  }

  it should "write with timestamp" in {
    wr1.write(Test1("nm", Some("sn"), 65, 1438715114318570484L)) shouldEqual "name=nm,surname=sn age=65 1438715114318570484"
  }
}
