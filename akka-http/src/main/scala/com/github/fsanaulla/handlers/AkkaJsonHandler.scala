package com.github.fsanaulla.handlers

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.github.fsanaulla.core.handlers.JsonHandler
import com.github.fsanaulla.utils.AkkaContentTypes.AppJson
import spray.json.JsObject

import scala.concurrent.Future

private[fsanaulla] trait AkkaJsonHandler extends JsonHandler[HttpResponse] {

  protected implicit val mat: ActorMaterializer

  override def getJsBody(response: HttpResponse): Future[JsObject] = {
    Unmarshal(response.entity.withContentType(AppJson)).to[JsObject]
  }
}
