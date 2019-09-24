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

import java.nio.ByteBuffer

import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import org.typelevel.jawn.ast._

import scala.util.{Failure, Success}

package object jawn {
  implicit def jv2Int(jv: JValue): Int = jv.asInt

  implicit def jv2Long(jv: JValue): Long = jv.asLong

  implicit def jv2Double(jv: JValue): Double = jv.asDouble

  implicit def jv2Boolean(jv: JValue): Boolean = jv.asBoolean

  implicit def jv2String(jv: JValue): String = jv.asString

  /** Extension to simplify parsing JAWN AST */
  implicit final class RichJValue(private val jv: JValue) extends AnyVal {

    def arrayValue: ErrorOr[Array[JValue]] = jv match {
      case JArray(arr) => Right(arr)
      case other =>
        Left(new WrongValueException("array", other.toString()))
    }

    def firstResult: Option[JValue] = results match {
      case JArray(vs) => vs.headOption
      case _          => None
    }

    def firstValue: Option[JValue] = values match {
      case JArray(vs) => vs.headOption
      case _          => None
    }

    def valuesArray: Option[Array[JValue]] = values match {
      case JArray(vs) => Some(vs)
      case _          => None
    }

    def seriesArray: Option[Array[JValue]] = series match {
      case JArray(vs) => Some(vs)
      case _          => None
    }

    def resultsArray: Option[Array[JValue]] = results match {
      case JArray(vs) => Some(vs)
      case _          => None
    }

    def firstSeries: Option[JValue] = series match {
      case JArray(vs) => vs.headOption
      case _          => None
    }

    def array: Option[JArray] = jv match {
      case ja: JArray => Some(ja)
      case _          => None
    }

    def obj: Option[JObject] = jv match {
      case jo: JObject => Some(jo)
      case _           => None
    }

    def series: JValue  = jv.get("series")
    def results: JValue = jv.get("results")
    def values: JValue  = jv.get("values")
    def tags: JValue    = jv.get("tags")
  }

  implicit final class RichJParser(private val jp: JParser.type) extends AnyVal {

    def parseFromStringEither(str: String): ErrorOr[JValue] = {
      jp.parseFromString(str) match {
        case Success(value)     => Right(value)
        case Failure(exception) => Left(exception)
      }
    }

    def parseFromByteBufferEither(data: ByteBuffer): ErrorOr[JValue] = {
      jp.parseFromByteBuffer(data) match {
        case Success(value)     => Right(value)
        case Failure(exception) => Left(exception)
      }
    }

    def parseFromStringOrNull(str: String): JValue = jp.parseFromString(str) match {
      case Success(value) => value
      case _              => JNull
    }
  }

}
