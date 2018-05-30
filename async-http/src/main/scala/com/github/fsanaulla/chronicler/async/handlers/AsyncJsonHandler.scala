package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.core.handlers.JsonHandler
import com.github.fsanaulla.core.model.Executable
import com.softwaremill.sttp.Response
import jawn.ast.{JParser, JValue}
import com.github.fsanaulla.core.utils.Extensions.RichJValue

import scala.concurrent.Future

private[fsanaulla] trait AsyncJsonHandler extends JsonHandler[Future, Response[JValue]] with Executable {

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
