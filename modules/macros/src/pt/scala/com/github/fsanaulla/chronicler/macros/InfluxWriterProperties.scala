package com.github.fsanaulla.chronicler.macros

import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.Checkers
import org.typelevel.jawn.ast.JArray

class InfluxWriterProperties extends AnyFlatSpec with Checkers with InfluxFormat with EitherValues {

  it should "generate InfluxWriter" in {
    check { t: Test =>
      t.surname match {
        case Some(v) if v.isEmpty =>
          wr.write(t).isLeft == influxWrite(t).isLeft
        case _ =>
          if (t.name.isEmpty) wr.write(t).isLeft == influxWrite(t).isLeft
          else wr.write(t).value == influxWrite(t).right.get
      }
    }
  }

  it should "generate InfluxReader" in {
    check { ja: JArray =>
      rd.read(ja).value == influxRead(ja).value
    }
  }
}
