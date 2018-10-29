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

package com.github.fsanaulla.chronicler.core.utils

import jawn.ast.{JArray, JObject, JValue}

private[chronicler] object Extensions {

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

  implicit final class RichString(private val str: String) extends AnyVal {
    def escapeFull: String = str.replaceAll("([ ,=])", "\\\\$1")
    def escape: String = str.replaceAll("([ ,])", "\\\\$1")
  }
}

