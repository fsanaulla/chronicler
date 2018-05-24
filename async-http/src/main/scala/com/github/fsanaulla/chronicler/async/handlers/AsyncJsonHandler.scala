package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.core.handlers.json.JsonHandler
import com.softwaremill.sttp.Response
import jawn.ast.{JParser, JValue}

import scala.concurrent.Future

private[fsanaulla] trait AsyncJsonHandler extends JsonHandler[Future, Response[JValue]] {

  override def getResponseBody(response: Response[JValue]): Future[JValue] = {
    response.body match {
      case Right(js) => Future.successful(js)
      case Left(str) => Future.fromTry(JParser.parseFromString(str))
    }
  }

  override def getResponseError(jsBody: Future[JValue]): Future[String] =
    jsBody.map(_.get("error").asString)

}
