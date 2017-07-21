package com.fsanaulla.utils

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.fsanaulla.model.TypeAlias.{InfluxPoint, InfluxQueryResult}
import com.fsanaulla.utils.ContentTypes.appJson
import spray.json.{JsArray, JsObject, JsValue}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by fayaz on 12.07.17.
  */
trait DatabaseHelper extends JsonSupport {

  def toInfluxPoint(measurement: String, serializedEntity: String): String = measurement + "," + serializedEntity

  def toInfluxPoints(measurement: String, serializedEntitys: Seq[String]): String = serializedEntitys.map(s => measurement + "," + s).mkString("\n")

  def unmarshalBody(response: HttpResponse)(implicit mat: ActorMaterializer): Future[JsObject] = {
    Unmarshal(response.entity.withContentType(appJson)).to[JsObject]
  }

  def singleQueryResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[Seq[InfluxPoint]] = {
    unmarshalBody(response)
      .map(_.getFields("results").head.convertTo[Seq[JsObject]].head)
      .map(_.fields("series").convertTo[Seq[JsObject]].head)
      .map(_.fields("values").convertTo[Seq[InfluxPoint]])
  }

  def bulkQueryResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[Seq[InfluxQueryResult]] = {
    unmarshalBody(response)
      .map(_.getFields("results").head.convertTo[Seq[JsObject]])
      .map(_.map(_.getFields("series").head.convertTo[Seq[JsObject]].head))
      .map(_.map(_.getFields("values").head.convertTo[Seq[JsArray]]))
  }
}
