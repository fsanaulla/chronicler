package com.github.fsanaulla.chronicler.urlhttp.handlers

import com.github.fsanaulla.chronicler.core.handlers.RequestHandler
import com.github.fsanaulla.chronicler.urlhttp.utils.Aliases.Request
import com.github.fsanaulla.chronicler.urlhttp.utils.ResponseFormats.asJson
import com.softwaremill.sttp.{Response, SttpBackend, Uri, sttp}
import jawn.ast.JValue

import scala.language.implicitConversions
import scala.util.Try

private[fsanaulla] trait UrlRequestHandler extends RequestHandler[Try, Request, Response[JValue], Uri] {
  protected implicit val backend: SttpBackend[Try, Nothing]

  override implicit def req(uri: Uri): Request = sttp.get(uri).response(asJson)
  override def execute(request: Request): Try[Response[JValue]] = request.send()
}
