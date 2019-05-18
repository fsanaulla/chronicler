package com.github.fsanaulla.chronicler.macros

import jawn.ast.JArray
import org.scalatest.FlatSpec
import org.scalatestplus.scalacheck.Checkers

class InfluxWriterProperties extends FlatSpec with Checkers with InfluxFormat {

  it should "generate InfluxWriter" in {
    check { t: Test =>
      t.surname match {
        case Some(v) if v.isEmpty =>
          wr.write(t).isLeft == influxWrite(t).isLeft
        case _ =>
          if (t.name.isEmpty) wr.write(t).isLeft == influxWrite(t).isLeft
          else wr.write(t).right.get == influxWrite(t).right.get
      }
    }
  }

  it should "generate InfluxReader" in {
    check { ja: JArray =>
      rd.read(ja).right.get == influxRead(ja).right.get
    }
  }
}
