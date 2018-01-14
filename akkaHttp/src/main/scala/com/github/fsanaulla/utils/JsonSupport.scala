package com.github.fsanaulla.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.github.fsanaulla.model.InfluxReader
import com.github.fsanaulla.utils.AkkaContentTypes.AppJson
import spray.json.{DefaultJsonProtocol, JsArray, JsObject, JsValue}

import scala.concurrent.Future

/**
  * Created by fayaz on 12.07.17.
  */
private[fsanaulla] object JsonSupport
  extends SprayJsonSupport
    with DefaultJsonProtocol {

  type Point = JsArray

  def getJsBody(response: HttpResponse)(implicit mat: ActorMaterializer): Future[JsObject] = {
    Unmarshal(response.entity.withContentType(AppJson)).to[JsObject]
  }

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

  def getInfluxPoints(js: JsObject): Seq[Point] = {
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
