package com.github.fsanaulla.core.utils

import jawn.ast.{JArray, JObject, JValue}

private[fsanaulla] object Extensions {

  implicit class RichJValue(private val jv: JValue) extends AnyVal {
    def arrayValue: Option[Array[JValue]] = jv match {
      case JArray(arr) => Some(arr)
      case _ => None
    }

    def array: Option[JArray] = jv match {
      case ja: JArray => Some(ja)
      case _ => None
    }

    def obj: Option[JObject] = jv match {
      case jo: JObject => Some(jo)
      case _ => None
    }
  }
}

