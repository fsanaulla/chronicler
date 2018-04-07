package com.github.fsanaulla.core.handlers

import com.github.fsanaulla.core.model.InfluxReader
import com.github.fsanaulla.core.utils.Extensions.RichJValue
import jawn.ast.{JArray, JValue}

import scala.concurrent.Future
import scala.reflect.ClassTag

/**
  * Created by fayaz on 12.07.17.
  */
private[fsanaulla] trait JsonHandler[R] {

  /***
    * Extracting JSON from Response
    * @param response - Response
    * @return - Extracted JSON
    */
  def getJsBody(response: R): Future[JValue]

  //todo: test
  def getOptBulkInfluxValue(js: JValue): Option[Array[Array[JArray]]] = {
    js.get("results").arrayValue
      .map(_.flatMap(_.get("series").arrayValue.flatMap(_.headOption)))
      .map(_.flatMap(_.get("values").arrayValue.map(_.flatMap(_.array))))
  }

  //todo: test
  def getOptInfluxPoints(js: JValue): Option[Array[JArray]] = {
    js.get("results")
      .arrayValue
      .flatMap(_.headOption)
      .flatMap(_.get("series").arrayValue.flatMap(_.headOption))
      .flatMap(_.get("values").arrayValue.flatMap(_.headOption))
      .flatMap(_.arrayValue.map(_.flatMap(_.array)))
  }

  //todo: test
  def getOptInfluxInfo[T: ClassTag](js: JValue)(implicit reader: InfluxReader[T]): Option[Array[(String, Array[T])]] = {
    js.get("results")
      .arrayValue.flatMap(_.headOption)
      .flatMap(_.get("series").arrayValue.flatMap(_.headOption))
      .flatMap(_.arrayValue)
      .map(_.flatMap(_.obj))
      .map(_.map { obj =>
        val dbName = obj.get("name").asString
        val cqInfo = obj
          .get("values")
          .arrayValue
          .flatMap(_.headOption)
          .flatMap(_.arrayValue)
          .map(_.flatMap(_.array))
          .map(_.map(reader.read))
          .getOrElse(Array.empty[T])

        dbName -> cqInfo
      })
  }
}
