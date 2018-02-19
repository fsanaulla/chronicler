package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.chronicler.async.utils.Extensions.RichEither
import com.github.fsanaulla.core.handlers.JsonHandler
import com.softwaremill.sttp.Response
import spray.json.{DeserializationException, JsObject}

import scala.concurrent.Future

private[fsanaulla] trait AsyncJsonHandler extends JsonHandler[Response[JsObject]] {
  override def getJsBody(response: Response[JsObject]): Future[JsObject] =
    response.body.toFuture(DeserializationException("Can't extract body"))
}
