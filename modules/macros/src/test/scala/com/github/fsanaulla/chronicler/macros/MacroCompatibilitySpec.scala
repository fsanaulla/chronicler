package com.github.fsanaulla.chronicler.macros

import com.github.fsanaulla.chronicler.testing.BaseSpec
import com.github.fsanaulla.chronicler.core.model.Point
import com.github.fsanaulla.chronicler.core.model.InfluxWriter
import com.github.fsanaulla.chronicler.macros.annotations.writer.escape
import org.scalatest.EitherValues
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag, timestamp}

class MacroCompatibilitySpec extends BaseSpec with EitherValues {

  "Macro generate code" - {

    "should" - {

      "be compatible with point" - {

        "with tag set" in {
          final case class Test(@tag t1: String, @field f1: Int)
          val wr = Influx.writer[Test]
          val p  = Point("test1").addTag("t1", "1").addField("f1", 5)

          ("test1," + wr.write(Test("1", 5)).value) shouldEqual p.serialize
        }

        "tagless" in {
          final case class Test(@field f1: Int)
          val wr = Influx.writer[Test]
          val p  = Point("test1").addField("f1", 5)

          ("test1 " + wr.write(Test(5)).value) shouldEqual p.serialize
        }
      }
    }
  }
}
