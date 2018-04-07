package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.core.handlers.JsonHandler
import com.github.fsanaulla.core.model.Executable
import com.softwaremill.sttp.Response
import jawn.ast.{JParser, JValue}

import scala.concurrent.Future

private[fsanaulla] trait AsyncJsonHandler extends JsonHandler[Response[JValue]] with Executable {

  override def getJsBody(response: Response[JValue]): Future[JValue] = {
    response.body match {
      case Right(js) => Future.successful(js)
      case Left(str) => Future.fromTry(JParser.parseFromString(str))
    }
  }
}
