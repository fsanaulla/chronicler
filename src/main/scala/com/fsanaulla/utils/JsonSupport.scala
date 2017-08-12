package com.fsanaulla.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.fsanaulla.model.{ContinuousQuery, ContinuousQueryInfo, InfluxReader}
import com.fsanaulla.utils.ContentTypes.appJson
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future


/**
  * Created by fayaz on 12.07.17.
  */
private[fsanaulla] object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  import spray.json._

  def unmarshalBody(response: HttpResponse)(implicit mat: ActorMaterializer): Future[JsObject] = {
    Unmarshal(response.entity.withContentType(appJson)).to[JsObject]
  }

  def getInfluxValue(js: JsObject): Seq[JsArray] = {
    js.getFields("results").head.convertTo[Seq[JsObject]].head
      .getFields("series") match {
      case seq: Seq[JsValue] if seq.nonEmpty =>
        seq.head.convertTo[Seq[JsObject]].head
          .getFields("values") match {
          case seq: Seq[JsValue] if seq.nonEmpty =>
            seq.head.convertTo[Seq[JsArray]]
          case _ => Nil
        }
      case _ => Nil
    }
  }

  def getInfluxCQInfo(js: JsObject)(implicit reader: InfluxReader[ContinuousQuery]): Seq[ContinuousQueryInfo] = {
    js.getFields("results").head.convertTo[Seq[JsObject]].head
      .getFields("series") match {
      case seq: Seq[JsValue] if seq.nonEmpty =>
        seq.head.convertTo[Seq[JsObject]]
          .map { obj =>
            val dbName = obj.getFields("name").head.convertTo[String]
            val cqInfo = obj.getFields("values") match {
              case seq: Seq[JsValue] if seq.nonEmpty =>
                seq.head.convertTo[Seq[JsArray]].map(reader.read)
              case _ => Nil
            }

            ContinuousQueryInfo(dbName, cqInfo)
          }
      case _ => Nil
    }
  }
}
