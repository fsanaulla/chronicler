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

package com.github.fsanaulla.chronicler.core.typeclasses

import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.core.utils.Extensions.RichJValue
import jawn.ast.{JArray, JValue}

import scala.reflect.ClassTag

/***
  * Trait that define all necessary methods for handling JSON related operation
  *
  * @tparam A - Response type
  */
private[chronicler] trait JsonHandler[F[_], A] {

  /***
    * Extracting JSON from Response
    *
    * @param response - Response
    * @return         - Extracted JSON
    */
  private[chronicler] def getResponseBody(response: A): F[JValue]

  /**
    * Extract error message from response
    *
    * @param response - Response
    * @return         - Error Message
    */
  private[chronicler] def getResponseError(response: A): F[String]

  /**
    * Extract optional error message from response
    *
    * @param response - Response JSON body
    * @return         - optional error message
    */
  private[chronicler] def getOptResponseError(response: A): F[Option[String]]

  /**
    * Extract influx points from JSON, representede as Arrays
    *
    * @param js - JSON value
    * @return - optional array of points
    */
  private[chronicler] final def getOptQueryResult(js: JValue): Option[Array[JArray]] = {
    js.get("results").arrayValue.flatMap(_.headOption) // get head of 'results' field
      .flatMap(_.get("series").arrayValue.flatMap(_.headOption)) // get head of 'series' field
      .flatMap(_.get("values").arrayValue) // get array of jValue
      .map(_.flatMap(_.array)) // map to array of JArray
  }

  private[chronicler] final def getOptGropedResult(js: JValue): Option[Array[(Array[String], JArray)]] = {
    js.get("results").arrayValue.flatMap(_.headOption)
      .flatMap(_.get("series").arrayValue)
      .map(_.flatMap(_.obj))
      .map(_.map { obj =>
        val tags = obj.get("tags").obj.map(_.vs.values.map(_.asString).toArray.sorted)
        val values = obj
          .get("values")
          .arrayValue
          .flatMap(_.headOption)
          .flatMap(_.array)

        tags.getOrElse(Array.empty[String]) -> values.getOrElse(JArray.empty)
      })
  }

  /**
    * Extract bulk result from JSON
    * @param js - JSON value
    * @return - Array of points
    */
  private[chronicler] final def getOptBulkInfluxPoints(js: JValue): Option[Array[Array[JArray]]] = {
    js.get("results").arrayValue // get array from 'results' field
      .map(_.flatMap(_.get("series").arrayValue.flatMap(_.headOption))) // get head of 'series' field
      .map(_.flatMap(_.get("values").arrayValue.map(_.flatMap(_.array)))) // get 'values' array
  }

  /**
    * Extract Measurement name -> Measurement points array
    * @param js - JSON value
    * @return = array of meas name -> meas points
    */
  private[chronicler] final def getOptJsInfluxInfo(js: JValue): Option[Array[(String, Array[JArray])]] = {
    js.get("results").arrayValue.flatMap(_.headOption)
      .flatMap(_.get("series").arrayValue)
      .map(_.flatMap(_.obj))
      .map(_.map { obj =>
        val measurement = obj.get("name").asString
        val cqInfo = obj
          .get("values")
          .arrayValue
          .map(_.flatMap(_.array))
          .getOrElse(Array.empty[JArray])

        measurement -> cqInfo
      })
  }

  /**
    * Extract Measurement name and values from it from JSON
    * @param js - JSON value
    * @param rd - implicit reader for deserializing influx point to scala case classes
    * @tparam T - type of scala case class
    * @return - Array of pairs
    */
  private[chronicler] final def getOptInfluxInfo[T: ClassTag](js: JValue)(implicit rd: InfluxReader[T]): Option[Array[(String, Array[T])]] =
    getOptJsInfluxInfo(js).map(_.map { case (k, v) => k -> v.map(rd.read)})
}
