package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.core.handlers.JsonHandler
import com.softwaremill.sttp.Response
import spray.json.{JsObject, JsonParser}

import scala.concurrent.Future

private[fsanaulla] trait AsyncJsonHandler extends JsonHandler[Response[JsObject]] {
  override def getJsBody(response: Response[JsObject]): Future[JsObject] =
    response.body match {
      case Right(js) => Future.successful(js)
      case Left(str) => Future.successful(JsonParser(str).asJsObject)
    }
}
