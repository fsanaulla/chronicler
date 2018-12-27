/*
 * Copyright 2017-2018 Faiaz Sanaulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fsanaulla.chronicler.core

import _root_.jawn.ast.{JArray, JObject, JValue}

package object jawn {
  implicit def jv2Int(jv: JValue): Int = jv.asInt
  implicit def jv2Long(jv: JValue): Long = jv.asLong
  implicit def jv2Double(jv: JValue): Double = jv.asDouble
  implicit def jv2Boolean(jv: JValue): Boolean = jv.asBoolean
  implicit def jv2String(jv: JValue): String = jv.asString

  /** Extension to simplify parsing JAWN AST */
  implicit final class RichJValue(private val jv: JValue) extends AnyVal {
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
