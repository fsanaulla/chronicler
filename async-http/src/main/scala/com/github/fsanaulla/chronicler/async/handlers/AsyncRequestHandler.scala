package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.chronicler.async.utils.AsyncImplicits.str2strbody
import com.github.fsanaulla.chronicler.async.utils.Extensions.RichRequest
import com.github.fsanaulla.core.handlers.RequestHandler
import com.softwaremill.sttp._
import spray.json.{JsObject, JsonParser}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

private[fsanaulla] trait AsyncRequestHandler
  extends RequestHandler[Response[JsObject], Uri, Method, String] {

  protected implicit val backend: SttpBackend[Future, Nothing]
  protected val defaultMethod: Method = Method.POST

  private val asJson: ResponseAs[JsObject, Nothing] = {
    asString.map {
      case str: String if str.nonEmpty => JsonParser(str).asJsObject
      case _ => JsObject.empty
    }
  }

  override def readRequest(uri: Uri, method: Method, entity: Option[String] = None): Future[Response[JsObject]] = {
    (method: @unchecked) match {
      case Method.POST => sttp.post(uri).optBody(entity).response(asJson).send()
      case Method.GET => sttp.get(uri).optBody(entity).response(asJson).send()
    }
  }

  override def writeRequest(uri: Uri, method: Method = defaultMethod, entity: String): Future[Response[JsObject]] = {
    (method: @unchecked) match {
      case Method.POST => sttp.post(uri).body(entity).response(asJson).send()
      case Method.GET => sttp.get(uri).body(entity).response(asJson).send()
    }
  }
}
