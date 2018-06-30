package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.chronicler.async.utils.Aliases.Request
import com.github.fsanaulla.chronicler.async.utils.ResponseFormats.asJson
import com.github.fsanaulla.chronicler.core.handlers.RequestHandler
import com.softwaremill.sttp.{Response, SttpBackend, Uri, sttp}
import jawn.ast.JValue

import scala.concurrent.Future

private[async] trait AsyncRequestHandler
    extends RequestHandler[Future, Request, Response[JValue], Uri] {

  protected implicit val backend: SttpBackend[Future, Nothing]

  override implicit def req(uri: Uri): Request = sttp.get(uri).response(asJson)
  override def execute(request: Request): Future[Response[JValue]] = request.send()
}
