package com.github.fsanaulla.chronicler.macros.annotations

/***
  * Marker for declaring field value for influx point.
  * Can be only one of primitive type. Or container with primitive value, like Option[Int]
  */
// https://issues.scala-lang.org/browse/SI-7561
@scala.annotation.meta.getter
final class field extends scala.annotation.StaticAnnotation
