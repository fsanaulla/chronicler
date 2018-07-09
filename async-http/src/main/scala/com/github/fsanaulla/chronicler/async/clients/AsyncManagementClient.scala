package com.github.fsanaulla.chronicler.async.clients

import com.github.fsanaulla.chronicler.async.handlers.{AsyncQueryHandler, AsyncRequestHandler, AsyncResponseHandler}
import com.github.fsanaulla.chronicler.async.utils.Aliases.Request
import com.github.fsanaulla.chronicler.core.client.ManagementClient
import com.github.fsanaulla.chronicler.core.model.{InfluxCredentials, Mappable, WriteResult}
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend
import com.softwaremill.sttp.{Response, SttpBackend, Uri}
import jawn.ast.JValue

import scala.concurrent.{ExecutionContext, Future}

final class AsyncManagementClient(val host: String,
                                  val port: Int,
                                  val credentials: Option[InfluxCredentials])
                                 (implicit val ex: ExecutionContext)
  extends ManagementClient[Future, Request, Response[JValue], Uri, String]
    with AsyncRequestHandler
    with AsyncResponseHandler
    with AsyncQueryHandler
    with Mappable[Future, Response[JValue]]
    with AutoCloseable{

  protected implicit val backend: SttpBackend[Future, Nothing] =
    AsyncHttpClientFutureBackend()
  override def mapTo[B](resp: Future[Response[JValue]], f: Response[JValue] => Future[B]): Future[B] =
    resp.flatMap(f)

  override def close(): Unit =
    backend.close()

  override def ping: Future[WriteResult] =
    execute(buildQuery("/ping", Map.empty[String, String])).flatMap(toResult)
}
