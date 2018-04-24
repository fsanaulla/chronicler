package com.github.fsanaulla.macros

import com.github.fsanaulla.core.model.InfluxWriter
import com.github.fsanaulla.core.test.FlatSpecWithMatchers
import com.github.fsanaulla.macros.annotations.{field, tag}

class MacroWriterSpec extends FlatSpecWithMatchers {

  case class Test(@tag name: String,
                  @tag surname: Option[String],
                  @field age: Int)

  val wr: InfluxWriter[Test] = Macros.writer[Test]

  "Macros.writer" should "write with None" in {
    wr.write(Test("nm", None, 65)) shouldEqual "name=nm age=65"
  }

  it should "write with Some" in {
    wr.write(Test("nm", Some("sn"), 65)) shouldEqual "name=nm,surname=sn age=65"
  }
}
