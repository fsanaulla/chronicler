package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.chronicler.async.utils.AsyncImplicits.str2strbody
import com.github.fsanaulla.chronicler.async.utils.Extensions.RichRequest
import com.github.fsanaulla.core.handlers.RequestHandler
import com.softwaremill.sttp._
import jawn.ast.{JNull, JParser, JValue}

import scala.concurrent.Future
import scala.util.Success

private[fsanaulla] trait AsyncRequestHandler
  extends RequestHandler[Response[JValue], Uri, Method, String] {

  protected implicit val backend: SttpBackend[Future, Nothing]
  protected val defaultMethod: Method = Method.POST

  private def asJson: ResponseAs[JValue, Nothing] = {
    asString.map(JParser.parseFromString)
      .map {
        case Success(jv) => jv
        case _ => JNull
      }
  }

  override def readRequest(
                            uri: Uri,
                           method: Method,
                           entity: Option[String] = None): Future[Response[JValue]] = (method: @unchecked) match {
    case Method.POST => sttp.post(uri).optBody(entity).response(asJson).send()
    case Method.GET => sttp.get(uri).response(asJson).send()
  }

  override def writeRequest(
                             uri: Uri,
                            method: Method = defaultMethod,
                            entity: String): Future[Response[JValue]] = (method: @unchecked) match {
    case Method.POST => sttp.post(uri).body(entity).response(asJson).send()
    case Method.GET => sttp.get(uri).body(entity).response(asJson).send()
  }
}
