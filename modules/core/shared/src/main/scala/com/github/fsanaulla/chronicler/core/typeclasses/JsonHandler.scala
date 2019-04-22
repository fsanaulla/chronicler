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

package com.github.fsanaulla.chronicler.core.typeclasses

import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.either
import com.github.fsanaulla.chronicler.core.headers.{buildHeader, versionHeader}
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.model.{InfluxDBInfo, InfluxReader, ParsingException}
import com.github.fsanaulla.chronicler.core.typeclasses.JsonHandler._
import jawn.ast.{JArray, JValue}

import scala.reflect.ClassTag

/***
  * Trait that define all necessary methods for handling JSON related operation
  *
  * @tparam A - Response type
  */
trait JsonHandler[A] {

  // extract reponse http code
  def responseCode(response: A): Int

  /***
    * Extract response headers
    */
  def responseHeader(response: A): Seq[(String, String)]

  /***
    * Extracting JSON from Response
    */
  def responseBody(response: A): ErrorOr[JValue]

  final def databaseInfo(response: A): ErrorOr[InfluxDBInfo] = {
    val headers = responseHeader(response)
    val result = for {
      build   <- headers.collectFirst { case (k, v) if k == buildHeader   => v }
      version <- headers.collectFirst { case (k, v) if k == versionHeader => v }
    } yield InfluxDBInfo(build, version)

    result.toRight(new ParsingException(s"Can't find $buildHeader or $versionHeader"))
  }

  /**
    * Extract error message from response
    *
    * @param response - Response
    * @return         - Error Message
    */
  final def responseErrorMsg(response: A): ErrorOr[String] =
    responseBody(response).map(_.get("error").asString)

  /**
    * Extract optional error message from response
    *
    * @param response - Response JSON body
    * @return         - optional error message
    */
  final def responseErrorMsgOpt(response: A): ErrorOr[Option[String]] =
    responseBody(response)
      .flatMap(_.get("results").arrayValue.flatMap(_.headRight(new NoSuchElementException("results[0]"))))
      .map(_.get("error").getString)

  /**
    * Extract influx points from JSON, representede as Arrays
    *
    * @param js - JSON value
    * @return   - optional array of points
    */
  final def queryResult(js: JValue): ErrorOr[Array[JArray]] = {
    js.get("results").arrayValue.flatMap(_.headRight(new NoSuchElementException("results[0]"))) // get head of 'results' field
      .flatMap(_.get("series").arrayValue.flatMap(_.headRight(new NoSuchElementException("series[0]")))) // get head of 'series' field
      .flatMap(_.get("values").arrayValue) // get array of jValue
      .flatMap(arr => either.array[Throwable, JArray](arr.map(_.array))) // map to array of JArray
  }

  final def gropedResult(js: JValue): ErrorOr[Array[(Array[String], JArray)]] = {
    js.get("results").arrayValue.flatMap(_.headRight(new NoSuchElementException("results[0]")))
      .flatMap(_.get("series").arrayValue)
      .map(_.map(_.obj))
      .map(either.array)
      .joinRight
      .map(_.map { obj =>
        val tags = obj.get("tags").obj.map(_.vs.values.map(_.asString).toArray.sorted)
        val values = obj
          .get("values")
          .arrayValue
          .flatMap(_.headRight(new NoSuchElementException("values[0]")))
          .flatMap(_.array)

        tags.getOrElse(Array.empty[String]) -> values.getOrElse(JArray.empty)
      })
  }

  /**
    * Extract bulk result from JSON
    * @param js - JSON value
    * @return   - Array of points
    */
  final def bulkResult(js: JValue): ErrorOr[Array[Array[JArray]]] = {
    js.get("results").arrayValue // get array from 'results' field
      .map(_.map(_.get("series").arrayValue.flatMap(_.headRight(new NoSuchElementException("series[0]"))))) // get head of 'series' field
      .map(either.array)
      .joinRight
      .map(_.map(_.get("values").arrayValue))
      .map(either.array)
      .joinRight
      .map(_.map(_.map(_.array)))
      .map(_.map(either.array))
      .map(either.array)
      .joinRight
  }

  /**
    * Extract Measurement name -> Measurement points array
    * @param js - JSON value
    * @return   - array of meas name -> meas points
    */
  final def groupedSystemInfoJs(js: JValue): ErrorOr[Array[(String, Array[JArray])]] = {
    js.get("results").arrayValue.flatMap(_.headRight(new NoSuchElementException("results[0]")))
      .flatMap(_.get("series").arrayValue)
      .map(_.map(_.obj))
      .map(either.array)
      .joinRight
      .map(_.map { obj =>
        val measurement = obj.get("name").asString
        val cqInfo = obj
          .get("values")
          .arrayValueOr(Array.empty)
          .map(_.array)

        either.array(cqInfo).map(measurement -> _)
      })
      .map(either.array)
      .joinRight
  }

  /**
    * Extract Measurement name and values from it from JSON
    * @param js - JSON value
    * @return   - Array of pairs
    */
  final def groupedSystemInfo[T: ClassTag: InfluxReader](js: JValue): ErrorOr[Array[(String, Array[T])]] =
    groupedSystemInfoJs(js)
      .map(arr =>
        arr.map {
          case (k, v) =>
            either.array[Throwable, T](v.map(InfluxReader[T].read)).map(k -> _)
        })
      .map(either.array)
      .joinRight
}

object JsonHandler {
  implicit final class ArrayOps[A](private val arr: Array[A]) extends AnyVal {
    def headRight(left: => Throwable): ErrorOr[A] = arr.headOption.toRight(left)
  }
}
