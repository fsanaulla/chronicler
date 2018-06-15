package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.chronicler.core.handlers.RequestHandler
import com.softwaremill.sttp._
import jawn.ast.{JNull, JParser, JValue}

import scala.concurrent.Future
import scala.util.Success

private[async] trait AsyncRequestHandler
    extends RequestHandler[Future, Response[JValue], Uri, String] {

  protected implicit val backend: SttpBackend[Future, Nothing]

  private def asJson: ResponseAs[JValue, Nothing] = {
    asString
      .map(JParser.parseFromString)
      .map {
        case Success(jv) => jv
        case _           => JNull
      }
  }

  override def readRequest(
      uri: Uri,
      entity: Option[String] = None): Future[Response[JValue]] =
    sttp.get(uri).response(asJson).send()

  override def writeRequest(uri: Uri,
                            entity: String): Future[Response[JValue]] =
    sttp.post(uri).body(entity).response(asJson).send()
}
