package com.gtihub.fsanaulla.chronicler.core

import com.github.fsanaulla.chronicler.core.model.Point
import org.scalatest.{FlatSpec, Matchers}

class PointSpec extends FlatSpec with Matchers {

  "Point" should "be correctly serialized" in {

    val ts = System.currentTimeMillis() * 1000000 + System.nanoTime()

    val p = Point("test")
      .addTag("city", "London")
      .addField("name", "Jivi")
      .addField("age", 22)
      .addField("adult", value = true)
      .addField("weight", 75.6)
      .addTimestamp(ts)

    p.serialize shouldEqual "test,city=London name=\"Jivi\",age=22i,adult=true,weight=75.6" + s" $ts"
  }
}
