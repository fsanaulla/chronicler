package com.fsanaulla

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.fsanaulla.model.JsonSupport
import com.fsanaulla.utils.ContentTypes.appJson
import spray.json.{JsArray, JsObject}

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

  def toJson(response: HttpResponse): Future[Seq[JsArray]] = {
    Unmarshal(response.entity.withContentType(appJson))
      .to[JsObject]
      .map(_.getFields("results").head.convertTo[Seq[JsObject]].head)
      .map(_.fields("series").convertTo[Seq[JsObject]].head)
      .map(_.fields("values").convertTo[Seq[JsArray]])
  }
}
