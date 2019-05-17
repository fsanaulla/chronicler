package com.github.fsanaulla.chronicler.macros

import jawn.ast.JArray
import org.scalatest.FlatSpec
import org.scalatest.prop.Checkers

class InfluxWriterProperties extends FlatSpec with Checkers with InfluxFormat {

  it should "generate InfluxWriter" in {
    check { t: Test =>
      if (t.name.isEmpty) {
        val f = wr.write(t).isLeft
        f && f == influxWrite(t).isLeft
      } else wr.write(t).right.get == influxWrite(t).right.get
    }
  }

  it should "generate InfluxReader" in {
    check { ja: JArray =>
      rd.read(ja).right.get == influxRead(ja).right.get
    }
  }
}
