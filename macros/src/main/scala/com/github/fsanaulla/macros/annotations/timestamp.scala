package com.github.fsanaulla.macros.annotations

import scala.annotation.compileTimeOnly

/** Mark field that will be used as timestamp in InfluxDB.
  * It can be optional, in this case will be used default
  * InfluxDB time, that equal to now.*/
@compileTimeOnly("Compile type annotation")
@scala.annotation.meta.getter
final class timestamp extends scala.annotation.StaticAnnotation
