package com.github.fsanaulla.macros

import com.github.fsanaulla.core.model.InfluxWriter
import com.github.fsanaulla.core.test.FlatSpecWithMatchers
import com.github.fsanaulla.macros.annotations.{field, tag}

class MacroWriterSpec extends FlatSpecWithMatchers {
  case class Test(@tag name: String, @field age: Int, @field grade: Option[Double])
  val wr: InfluxWriter[Test] = Macros.writer[Test]

  "Macros.writer" should "write with None" in {
    wr.write(Test("tName", 65, None)) shouldEqual "name=tName age=65"
  }

  it should "write with Some" in {
    wr.write(Test("tName", 65, Some(2.0))) shouldEqual "name=tName age=65 grade=2.0"
  }
}
