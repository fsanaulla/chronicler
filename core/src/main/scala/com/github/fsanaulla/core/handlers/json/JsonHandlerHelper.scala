package com.github.fsanaulla.core.handlers.json

import com.github.fsanaulla.core.model.{Executable, InfluxReader}
import com.github.fsanaulla.core.utils.Extensions.RichJValue
import jawn.ast.{JArray, JValue}

import scala.concurrent.Future
import scala.reflect.ClassTag

/***
  * Predefined JSON extractors
  */
private[core] trait JsonHandlerHelper[R] extends Executable {
  self: JsonHandler[R] =>

  /**
    * Extract influx points from JSON, representede as Arrays
    * @param js - JSON value
    * @return - optional array of points
    */
  def getOptInfluxPoints(js: JValue): Option[Array[JArray]] = {
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
  def getOptBulkInfluxPoints(js: JValue): Option[Array[Array[JArray]]] = {
    js.get("results").arrayValue // get array from 'results' field
      .map(_.flatMap(_.get("series").arrayValue.flatMap(_.headOption))) // get head of 'series' field
      .map(_.flatMap(_.get("values").arrayValue.map(_.flatMap(_.array)))) // get 'values' array
  }

  /**
    * Extract Measurement name -> Measurement points array
    * @param js - JSON value
    * @return = array of meas name -> meas points
    */
  def getOptJsInfluxInfo(js: JValue): Option[Array[(String, Array[JArray])]] = {
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
  def getOptInfluxInfo[T: ClassTag](js: JValue)(implicit rd: InfluxReader[T]): Option[Array[(String, Array[T])]] = {
    getOptJsInfluxInfo(js).map(_.map { case (k, v) => k -> v.map(rd.read)})
  }

  /**
    * Extract error message from response
    * @param response - Response
    * @return         - Error Message
    */
  def getError(response: R): Future[String] =
    getJsBody(response).map(_.get("error").asString)

  /**
    * Extract optional error message from response
    * @param response - Response
    * @return         - optional error message
    */
  def getErrorOpt(response: R): Future[Option[String]] = {
    getJsBody(response)
      .map(_.get("results").arrayValue.flatMap(_.headOption))
      .map(_.flatMap(_.get("error").getString))
  }
}
