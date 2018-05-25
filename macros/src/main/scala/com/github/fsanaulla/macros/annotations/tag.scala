package com.github.fsanaulla.macros.annotations

import scala.annotation.compileTimeOnly

/***
  * Marker for declaring influx point tag's. Can be only String.
  */
// https://issues.scala-lang.org/browse/SI-7561
@compileTimeOnly("Compile type annotation")
@scala.annotation.meta.getter
final class tag extends scala.annotation.StaticAnnotation
