package com.github.fsanaulla.macros

import com.github.fsanaulla.core.model.InfluxWriter
import com.github.fsanaulla.core.test.utils.FlatSpecWithMatchers
import com.github.fsanaulla.macros.annotations.{field, tag}

class MacroWriterSpec extends FlatSpecWithMatchers {

  "Macros.writer" should "generate writer with fully annotated fields" in {
    case class Test(@tag name: String, @field age: Int)
    val wr: InfluxWriter[Test] = Macros.writer[Test]

    wr.write(Test("tName", 65)) shouldEqual "name=tName age=65"
  }

  it should "generate writer for partially annotated fields" in {
    case class Test(@tag name: String, age: Int)
    val wr: InfluxWriter[Test] = Macros.writer[Test]

    wr.write(Test("tName", 65)) shouldEqual "name=tName"
  }
}
