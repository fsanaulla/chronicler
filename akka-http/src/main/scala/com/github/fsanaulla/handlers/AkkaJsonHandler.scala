package com.github.fsanaulla.handlers

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.github.fsanaulla.utils.AkkaContentTypes.AppJson
import com.github.fsanaulla.utils.JsonHandler
import spray.json.JsObject

import scala.concurrent.Future

private[fsanaulla] trait AkkaJsonHandler extends JsonHandler[HttpResponse] {
  override def getJsBody(response: HttpResponse): Future[JsObject] = {
    Unmarshal(response.entity.withContentType(AppJson)).to[JsObject]
  }
}
