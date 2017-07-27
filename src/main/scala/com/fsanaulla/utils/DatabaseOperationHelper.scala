package com.fsanaulla.utils

import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import spray.json.{JsArray, JsObject, JsValue}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by fayaz on 12.07.17.
  */
object DatabaseOperationHelper extends JsonSupport {

  def toPoint(measurement: String, serializedEntity: String): String = measurement + "," + serializedEntity

  def toPoints(measurement: String, serializedEntitys: Seq[String]): String = serializedEntitys.map(s => measurement + "," + s).mkString("\n")

  def toSingleResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[Seq[JsArray]] = {
    unmarshalBody(response).map(getInfluxValue)
  }

  def toBulkResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[Seq[Seq[JsArray]]] = {
    unmarshalBody(response)
      .map(_.getFields("results").head.convertTo[Seq[JsObject]])
      .map(_.map(_.getFields("series").head.convertTo[Seq[JsObject]].head))
      .map(_.map(_.getFields("values").head.convertTo[Seq[JsArray]]))
  }
}
