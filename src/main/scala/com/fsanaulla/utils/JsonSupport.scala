package com.fsanaulla.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.fsanaulla.utils.ContentTypes.appJson
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future


/**
  * Created by fayaz on 12.07.17.
  */
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  import spray.json._

  def unmarshalBody(response: HttpResponse)(implicit mat: ActorMaterializer): Future[JsObject] = {
    Unmarshal(response.entity.withContentType(appJson)).to[JsObject]
  }

  def getInfluxValue(js: JsObject): Seq[JsArray] = {
    js.getFields("results").head.convertTo[Seq[JsObject]].head
      .getFields("series").head.convertTo[Seq[JsObject]].head
      .getFields("values").head.convertTo[Seq[JsArray]]
  }
}
