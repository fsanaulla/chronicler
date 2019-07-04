/*
 * Copyright 2017-2019 Faiaz Sanaulla
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

import _root_.jawn.ast._
import com.github.fsanaulla.chronicler.core.alias.ErrorOr

import scala.util.{Failure, Success}

package object jawn {
  implicit def jv2Int(jv: JValue): Int         = jv.asInt
  implicit def jv2Long(jv: JValue): Long       = jv.asLong
  implicit def jv2Double(jv: JValue): Double   = jv.asDouble
  implicit def jv2Boolean(jv: JValue): Boolean = jv.asBoolean
  implicit def jv2String(jv: JValue): String   = jv.asString

  /** Extension to simplify parsing JAWN AST */
  implicit final class RichJValue(private val jv: JValue) extends AnyVal {
    def arrayValue: ErrorOr[Array[JValue]] = jv match {
      case JArray(arr) => Right(arr)
      case other       =>
        Left(new WrongValueException("array", other.toString()))
    }

    def arrayValueOr(default: => Array[JValue]): Array[JValue] =
      arrayValue.right.getOrElse(default)

    def array: ErrorOr[JArray] = jv match {
      case ja: JArray => Right(ja)
      case other      => Left(new WrongValueException("array", other.toString()))
    }

    def obj: ErrorOr[JObject] = jv match {
      case jo: JObject => Right(jo)
      case other       => Left(new WrongValueException("object", other.toString()))
    }
  }

  implicit final class RichJParser(private val jp: JParser.type) extends AnyVal {
    def parseFromStringEither(str: String): ErrorOr[JValue] = {
      JParser.parseFromString(_) match {
        case Success(value)     => Right(value)
        case Failure(exception) => Left(exception)
      }
    }
  }
}
