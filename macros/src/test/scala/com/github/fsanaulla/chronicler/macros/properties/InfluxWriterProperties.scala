package com.github.fsanaulla.chronicler.macros.properties

import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object InfluxWriterProperties
  extends Properties("InfluxFormatter")
    with InfluxFormat {

  property("write") = forAll(gen) { t: Test =>
    fmt.write(t) == influxWrite(t)
  }

  property("read") = forAll(genJArr) { jarr =>
    fmt.read(jarr) == influxRead(jarr)
  }
}
