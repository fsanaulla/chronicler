package com.github.fsanaulla.macros

import com.github.fsanaulla.core.model.InfluxWriter
import org.scalatest.{FlatSpec, Matchers}

class MacroSpec extends FlatSpec with Matchers {

  case class Test(name: String, age: Int)

  "Macros" should "generate writer" in {
    implicit val wr: InfluxWriter[Test] = InfluxFormatter.writer[Test]

    def test(t: Test)(implicit wr: InfluxWriter[Test]) = {
      wr.write(t)
    }

    test(Test("tName", 65)) shouldEqual "name=tName age=65"
  }
}
