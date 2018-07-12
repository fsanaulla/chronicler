package com.github.fsanaulla.chronicler.urlhttp.handlers

import com.github.fsanaulla.chronicler.core.handlers.JsonHandler
import com.github.fsanaulla.chronicler.core.utils.Extensions.RichJValue
import com.softwaremill.sttp.Response
import jawn.ast.{JParser, JValue}

import scala.util.{Success, Try}

private[urlhttp] trait UrlJsonHandler extends JsonHandler[Try, Response[JValue]] {

  private[chronicler] override def getResponseBody(response: Response[JValue]): Try[JValue] = response.body match {
    case Right(js) => Success(js)
    case Left(str) => JParser.parseFromString(str)
  }

  private[chronicler] override def getResponseError(response: Response[JValue]): Try[String] =
    getResponseBody(response).map(_.get("error").asString)

  private[chronicler] override def getOptResponseError(response: Response[JValue]): Try[Option[String]] =
    getResponseBody(response)
      .map(_.get("results").arrayValue.flatMap(_.headOption))
      .map(_.flatMap(_.get("error").getString))

}
