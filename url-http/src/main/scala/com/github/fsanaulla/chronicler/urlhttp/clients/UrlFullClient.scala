package com.github.fsanaulla.chronicler.urlhttp.clients

import com.github.fsanaulla.chronicler.core.client.FullClient
import com.github.fsanaulla.chronicler.core.model.{InfluxCredentials, Mappable, WriteResult}
import com.github.fsanaulla.chronicler.urlhttp.api.{Database, Measurement}
import com.github.fsanaulla.chronicler.urlhttp.handlers.{UrlQueryHandler, UrlRequestHandler, UrlResponseHandler}
import com.github.fsanaulla.chronicler.urlhttp.utils.Aliases.Request
import com.softwaremill.sttp.{Response, SttpBackend, TryHttpURLConnectionBackend, Uri}
import jawn.ast.JValue

import scala.reflect.ClassTag
import scala.util.Try

final class UrlFullClient(val host: String,
                          val port: Int,
                          val credentials: Option[InfluxCredentials],
                          gzipped: Boolean)
  extends FullClient[Try, Request, Response[JValue], Uri, String]
    with UrlRequestHandler
    with UrlResponseHandler
    with UrlQueryHandler
    with Mappable[Try, Response[JValue]]
    with AutoCloseable {

  override def mapTo[B](resp: Try[Response[JValue]],
                        f: Response[JValue] => Try[B]): Try[B] =
    resp.flatMap(f)

  protected implicit val backend: SttpBackend[Try, Nothing] = TryHttpURLConnectionBackend()

  override def database(dbName: String): Database =
    new Database(host, port, credentials, dbName, gzipped)

  override def measurement[A: ClassTag](dbName: String,
                                        measurementName: String): Measurement[A] =
    new Measurement[A](host, port, credentials, dbName, measurementName, gzipped)

  override def ping: Try[WriteResult] =
    execute(buildQuery("/ping", Map.empty[String, String])).flatMap(toResult)

  override def close(): Unit = backend.close()
}
