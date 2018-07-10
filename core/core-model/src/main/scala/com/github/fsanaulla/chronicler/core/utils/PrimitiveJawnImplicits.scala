package com.github.fsanaulla.chronicler.core.utils

import jawn.ast.JValue

private[fsanaulla] object PrimitiveJawnImplicits {
  implicit def jv2Int(jv: JValue): Int = jv.asInt
  implicit def jv2Long(jv: JValue): Long = jv.asLong
  implicit def jv2Double(jv: JValue): Double = jv.asDouble
  implicit def jv2Boolean(jv: JValue): Boolean = jv.asBoolean
  implicit def jv2String(jv: JValue): String = jv.asString
}
