package com.github.fsanaulla.handlers

import com.github.fsanaulla.core.handlers.RequestHandler
import com.github.fsanaulla.utils.AsyncImplicits.str2strbody
import com.github.fsanaulla.utils.Extensions.RichRequest
import com.softwaremill.sttp._
import spray.json.{JsObject, JsonParser}

import scala.concurrent.Future

private[fsanaulla] trait AsyncRequestHandler
  extends RequestHandler[Response[JsObject], Uri, Method, String] {

  protected implicit val backend: SttpBackend[Future, Nothing]
  override val defaultMethod: Method = Method.POST

  private val asJson: ResponseAs[JsObject, Nothing] = asString.map(JsonParser(_)).map(_.asJsObject)

  override def readRequest(uri: Uri, method: Method, entity: Option[String] = None): Future[Response[JsObject]] = {
    method match {
      // todo: expanse methods list
      case Method.POST => sttp.post(uri).optBody(entity).response(asJson).send()
      case Method.GET => sttp.get(uri).optBody(entity).response(asJson).send()
    }
  }

  override def writeRequest(uri: Uri, method: Method, entity: String): Future[Response[JsObject]] = {
    method match {
      // todo: expanse methods list
      case Method.POST => sttp.post(uri).body(entity).response(asJson).send()
      case Method.GET => sttp.get(uri).body(entity).response(asJson).send()
    }
  }
}
