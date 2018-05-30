package com.github.fsanaulla.chronicler.urlhttp.handlers

import com.github.fsanaulla.core.handlers.RequestHandler
import com.softwaremill.sttp._
import jawn.ast.{JNull, JParser, JValue}

import scala.util.{Success, Try}

private[fsanaulla] trait UrlRequestHandler
  extends RequestHandler[Try, Response[JValue], Uri, String] {

  protected implicit val backend: SttpBackend[Try, Nothing]

  private def asJson: ResponseAs[JValue, Nothing] = {
    asString.map(JParser.parseFromString)
      .map {
        case Success(jv) => jv
        case _ => JNull
      }
  }

  override def readRequest(uri: Uri, entity: Option[String] = None): Try[Response[JValue]] =
    sttp.get(uri).response(asJson).send()

  override def writeRequest(uri: Uri, entity: String): Try[Response[JValue]] =
    sttp.post(uri).body(entity).response(asJson).send()
}
