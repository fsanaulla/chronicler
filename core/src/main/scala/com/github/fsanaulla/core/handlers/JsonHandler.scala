package com.github.fsanaulla.core.handlers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.github.fsanaulla.core.model.InfluxReader
import com.github.fsanaulla.core.utils.Extensions.RichJValue
import jawn.ast.{JArray, JObject, JValue}
import spray.json.{DefaultJsonProtocol, JsArray, JsObject, JsValue}

import scala.concurrent.Future

/**
  * Created by fayaz on 12.07.17.
  */
private[fsanaulla] trait JsonHandler[R] extends SprayJsonSupport with DefaultJsonProtocol {

  def getJsBody(response: R): Future[JsObject]

  def getBulkInfluxValue(js: JsObject): Seq[Seq[JsArray]] = {
    js.getFields("results")
      .head
      .convertTo[Seq[JsObject]]
      .flatMap(_
        .getFields("series")
        .headOption
        .flatMap(_.convertTo[Seq[JsObject]].headOption))
      .map(_
        .getFields("values")
        .headOption match {
          case Some(jsVal) => jsVal.convertTo[Seq[JsArray]]
          case _ => Nil
        }
      )
  }

  def getOptBulkInfluxValue(js: JObject): Option[Array[Array[JValue]]] = {
    js.get("results").arrayValue
      .map(_.flatMap(_.get("series").arrayValue.flatMap(_.headOption)))
      .map(_.flatMap(_.get("values").arrayValue))
  }

  def getInfluxPoints(js: JsObject): Seq[JsArray] = {
    js.getFields("results")
      .head
      .convertTo[Seq[JsObject]]
      .head
      .getFields("series") match {
      case seq: Seq[JsValue] if seq.nonEmpty =>
        seq.head
          .convertTo[Seq[JsObject]]
          .head
          .getFields("values") match {
          case seq: Seq[JsValue] if seq.nonEmpty =>
            seq.head.convertTo[Seq[JsArray]]
          case _ => Nil
        }
      case _ => Nil
    }
  }

  def getOptInfluxPoints(js: JObject): Option[Array[JArray]] = {
    js.get("results")
      .arrayValue
      .flatMap(_.headOption)
      .flatMap(_.get("series").arrayValue.flatMap(_.headOption))
      .flatMap(_.get("values").arrayValue.flatMap(_.headOption))
      .flatMap(_.arrayValue.map(_.flatMap(_.array)))
  }

  def getInfluxInfo[T](js: JsObject)(implicit reader: InfluxReader[T]): Seq[(String, Seq[T])] = {
    js.getFields("results")
      .head
      .convertTo[Seq[JsObject]]
      .head
      .getFields("series") match {
      case seq: Seq[JsValue] if seq.nonEmpty =>
        seq.head
          .convertTo[Seq[JsObject]]
          .map { obj =>
            val dbName = obj.getFields("name").head.convertTo[String]
            val cqInfo = obj.getFields("values") match {
              case seq: Seq[JsValue] if seq.nonEmpty =>
                seq.head.convertTo[Seq[JsArray]].map(reader.read)
              case _ => Nil
            }

            dbName -> cqInfo
          }
      case _ => Nil
    }
  }

  def getOptInfluxInfo[T](js: JObject)(implicit reader: InfluxReader[T]): Option[Array[(String, Array[T])]] = {
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
