package com.github.fsanaulla.chronicler.urlhttp.clients

import com.github.fsanaulla.chronicler.core.client.ManagementClient
import com.github.fsanaulla.chronicler.core.model.{InfluxCredentials, Mappable, WriteResult}
import com.github.fsanaulla.chronicler.urlhttp.handlers.{UrlQueryHandler, UrlRequestHandler, UrlResponseHandler}
import com.github.fsanaulla.chronicler.urlhttp.utils.Aliases.Request
import com.softwaremill.sttp.{Response, SttpBackend, TryHttpURLConnectionBackend, Uri}
import jawn.ast.JValue

import scala.util.Try

class UrlManagementClient(val host: String,
                          val port: Int,
                          val credentials: Option[InfluxCredentials])
  extends ManagementClient [Try, Request, Response[JValue], Uri, String]
    with UrlRequestHandler
    with UrlResponseHandler
    with UrlQueryHandler
    with Mappable[Try, Response[JValue]]
    with AutoCloseable {

  override protected implicit val backend: SttpBackend[Try, Nothing] =
    TryHttpURLConnectionBackend()

  override def mapTo[B](resp: Try[Response[JValue]], f: Response[JValue] => Try[B]): Try[B] =
    resp.flatMap(f)

  override def ping: Try[WriteResult] =
    execute(buildQuery("/ping", Map.empty[String, String])).flatMap(toResult)

  override def close(): Unit = backend.close()
}
