package com.github.fsanaulla.core.handlers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.github.fsanaulla.core.model.InfluxReader
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
      .map(_
        .getFields("series")
        .headOption match {
          case Some(jsVal) => jsVal.convertTo[Seq[JsObject]].head
          case _ => JsObject.empty
        })
      .map(_
        .getFields("values")
        .headOption match {
          case Some(jsVal) => jsVal.convertTo[Seq[JsArray]]
          case _ => Nil
        }
      )
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
}
