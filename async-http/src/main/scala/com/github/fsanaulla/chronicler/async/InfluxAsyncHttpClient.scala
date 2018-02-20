package com.github.fsanaulla.chronicler.async

import com.github.fsanaulla.chronicler.async.api.{Database, Measurement}
import com.github.fsanaulla.chronicler.async.handlers._
import com.github.fsanaulla.core.client.InfluxClient
import com.github.fsanaulla.core.model.{InfluxCredentials, Result}
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend
import com.softwaremill.sttp.{Method, Response, SttpBackend, Uri}
import spray.json.JsObject

import scala.concurrent.{ExecutionContext, Future}

private[fsanaulla] class InfluxAsyncHttpClient(val host: String,
                                               val port: Int,
                                               username: Option[String],
                                               password: Option[String])(implicit val ex: ExecutionContext)
  extends InfluxClient[Response[JsObject], Uri, Method, String]
    with AsyncRequestHandler
    with AsyncResponseHandler
    with AsyncQueryHandler {

  protected implicit val backend: SttpBackend[Future, Nothing] = AsyncHttpClientFutureBackend()
  protected implicit val credentials: InfluxCredentials = InfluxCredentials(username, password)

  /**
    *
    * @param dbName - database name
    * @return Database instance that provide non type safe operations
    */
  override def database(dbName: String): Database = new Database(host, port, dbName)

  /**
    *
    * @param dbName          - database name
    * @param measurementName - measurement name
    * @tparam A - Measurement's time series type
    * @return - Measurement instance of type [A]
    */
  override def measurement[A](dbName: String,
                              measurementName: String): Measurement[A] =
    new Measurement[A](host, port, dbName, measurementName)

  /**
    * Ping InfluxDB
    */
  override def ping(): Future[Result] =
    readRequest(buildQuery("/ping", Map.empty[String, String]), Method.GET).flatMap(toResult)

  /**
    * Close HTTP connection
    */
  override def close(): Unit = {
    backend.close()
  }
}
