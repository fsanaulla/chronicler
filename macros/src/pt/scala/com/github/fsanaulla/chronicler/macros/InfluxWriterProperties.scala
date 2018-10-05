package com.github.fsanaulla.chronicler.macros

import jawn.ast.JArray
import org.scalatest.FlatSpec
import org.scalatest.prop.Checkers

import scala.util.Try

class InfluxWriterProperties extends FlatSpec with Checkers with InfluxFormat {

  "Macros" should "generate InfluxWriter" in {
    check { t: Test =>
      if (t.name.isEmpty) {
        val f = Try(fmt.write(t)).isFailure
        f && f == Try(influxWrite(t)).isFailure
      } else fmt.write(t) == influxWrite(t)
    }
  }

  it should "generate InfluxReader" in {
    check { jarr: JArray =>
      fmt.read(jarr) == influxRead(jarr)
    }
  }
}
