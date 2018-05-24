package com.github.fsanaulla.core.handlers

import com.github.fsanaulla.core.model.InfluxReader
import jawn.ast.{JArray, JValue}
import com.github.fsanaulla.core.utils.Extensions.RichJValue

import scala.reflect.ClassTag

/***
  * Trait that define all necessary methods for handling JSON related operation
  * @tparam R - Response type
  */
private[fsanaulla] trait JsonHandler[M[_], R] {

  /***
    * Extracting JSON from Response
    * @param response - Response
    * @return         - Extracted JSON
    */
  def getResponseBody(response: R): M[JValue]

  /**
    * Extract error message from response
    * @param response - Response
    * @return         - Error Message
    */
  def getResponseError(response: R): M[String]

  /**
    * Extract optional error message from response
    * @param response - Response JSON body
    * @return         - optional error message
    */
  def getOptResponseError(response: R): M[Option[String]]

  /**
    * Extract influx points from JSON, representede as Arrays
    * @param js - JSON value
    * @return - optional array of points
    */
  final def getOptInfluxPoints(js: JValue): Option[Array[JArray]] = {
    js.get("results").arrayValue.flatMap(_.headOption) // get head of 'results' field
      .flatMap(_.get("series").arrayValue.flatMap(_.headOption)) // get head of 'series' field
      .flatMap(_.get("values").arrayValue) // get array of jValue
      .map(_.flatMap(_.array)) // map to array of JArray
  }

  /**
    * Extract bulk result from JSON
    * @param js - JSON value
    * @return - Array of points
    */
  final def getOptBulkInfluxPoints(js: JValue): Option[Array[Array[JArray]]] = {
    js.get("results").arrayValue // get array from 'results' field
      .map(_.flatMap(_.get("series").arrayValue.flatMap(_.headOption))) // get head of 'series' field
      .map(_.flatMap(_.get("values").arrayValue.map(_.flatMap(_.array)))) // get 'values' array
  }

  /**
    * Extract Measurement name -> Measurement points array
    * @param js - JSON value
    * @return = array of meas name -> meas points
    */
  final def getOptJsInfluxInfo(js: JValue): Option[Array[(String, Array[JArray])]] = {
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
  final def getOptInfluxInfo[T: ClassTag](js: JValue)(implicit rd: InfluxReader[T]): Option[Array[(String, Array[T])]] =
    getOptJsInfluxInfo(js).map(_.map { case (k, v) => k -> v.map(rd.read)})

}