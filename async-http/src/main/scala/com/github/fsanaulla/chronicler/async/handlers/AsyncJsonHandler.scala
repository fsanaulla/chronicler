package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.chronicler.core.handlers.JsonHandler
import com.github.fsanaulla.chronicler.core.model.Executable
import com.github.fsanaulla.chronicler.core.utils.Extensions.RichJValue
import com.softwaremill.sttp.Response
import jawn.ast.{JParser, JValue}

import scala.concurrent.Future

private[async] trait AsyncJsonHandler
    extends JsonHandler[Future, Response[JValue]]
    with Executable {

  override def getResponseBody(response: Response[JValue]): Future[JValue] = {
    response.body match {
      case Right(js) => Future.successful(js)
      case Left(str) => Future.fromTry(JParser.parseFromString(str))
    }
  }

  override def getResponseError(response: Response[JValue]): Future[String] =
    getResponseBody(response).map(_.get("error").asString)

  override def getOptResponseError(response: Response[JValue]): Future[Option[String]] =
    getResponseBody(response)
      .map(_.get("results").arrayValue.flatMap(_.headOption))
      .map(_.flatMap(_.get("error").getString))

}
