package com.github.fsanaulla.chronicler.macros.properties

import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

import scala.util.Try

object InfluxWriterProperties
  extends Properties("InfluxFormatter")
    with InfluxFormat {

  property("write") = forAll(gen) { t: Test =>

    if (t.name.isEmpty) {
      val f = Try(fmt.write(t)).isFailure
      f && f == Try(influxWrite(t)).isFailure
    } else {
      fmt.write(t) == influxWrite(t)
    }
  }

  property("read") = forAll(genJArr) { jarr =>
    fmt.read(jarr) == influxRead(jarr)
  }
}
