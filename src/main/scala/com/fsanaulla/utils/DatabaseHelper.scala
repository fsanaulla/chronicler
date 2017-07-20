package com.fsanaulla.utils

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.fsanaulla.utils.ContentTypes.appJson
import com.fsanaulla.utils.TypeAlias.{InfluxPoint, InfluxQueryResult}
import spray.json.{JsArray, JsObject, JsValue}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by fayaz on 12.07.17.
  */
trait DatabaseHelper extends JsonSupport {

  implicit val actorSystem: ActorSystem
  implicit val mat: ActorMaterializer

  def toInfluxPoint(measurement: String, serializedEntity: String): String = measurement + "," + serializedEntity

  def toInfluxPoints(measurement: String, serializedEntitys: Seq[String]): String = serializedEntitys.map(s => measurement + "," + s).mkString("\n")

  def unmarshalBody(response: HttpResponse): Future[JsObject] = {
    Unmarshal(response.entity.withContentType(appJson)).to[JsObject]
  }

  def singleQueryResult(response: HttpResponse): Future[Seq[InfluxPoint]] = {
    unmarshalBody(response)
      .map(_.getFields("results").head.convertTo[Seq[JsObject]].head)
      .map(_.fields("series").convertTo[Seq[JsObject]].head)
      .map(_.fields("values").convertTo[Seq[InfluxPoint]])
  }

  def bulkQueryResult(response: HttpResponse): Future[Seq[InfluxQueryResult]] = {
    unmarshalBody(response)
      .map(_.getFields("results").head.convertTo[Seq[JsObject]])
      .map(_.map(_.getFields("series").head.convertTo[Seq[JsObject]].head))
      .map(_.map(_.getFields("values").head.convertTo[Seq[JsArray]]))
  }
}
