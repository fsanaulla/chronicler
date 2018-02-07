package com.github.fsanaulla

import com.github.fsanaulla.api.{Database, Measurement}
import com.github.fsanaulla.core.api.management._
import com.github.fsanaulla.core.api.{DatabaseApi, MeasurementApi}
import com.github.fsanaulla.core.model.{HasCredentials, InfluxCredentials, Result}
import com.github.fsanaulla.handlers._
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend
import com.softwaremill.sttp.{Method, Response, SttpBackend, Uri, _}
import spray.json.JsObject

import scala.concurrent.{ExecutionContext, Future}

private[fsanaulla] class InfluxAsyncHttpClient(host: String,
                                               port: Int,
                                               username: Option[String],
                                               password: Option[String])(implicit val ex: ExecutionContext)
  extends AsyncRequestHandler
    with AsyncResponseHandler
    with AsyncQueryHandler
    with HasCredentials
    with SystemManagement[String]
    with DatabaseManagement[Response[JsObject], Uri, Method, String]
    with UserManagement[Response[JsObject], Uri, Method, String]
    with RetentionPolicyManagement[Response[JsObject], Uri, Method, String]
    with ContinuousQueryManagement[Response[JsObject], Uri, Method, String]
    with ShardManagement[Response[JsObject], Uri, Method, String]
    with SubscriptionManagement[Response[JsObject], Uri, Method, String]{

  protected implicit val backend: SttpBackend[Future, Nothing] = AsyncHttpClientFutureBackend()
  protected implicit val credentials: InfluxCredentials = InfluxCredentials(username, password)

  /**
    *
    * @param dbName - database name
    * @return Database instance that provide non type safe operations
    */
  override def database(dbName: String): DatabaseApi[String] = new Database(dbName)

  /**
    *
    * @param dbName          - database name
    * @param measurementName - measurement name
    * @tparam A - Measurement's time series type
    * @return - Measurement instance of type [A]
    */
  override def measurement[A](dbName: String,
                              measurementName: String): MeasurementApi[A, String] =
    new Measurement[A](dbName, measurementName)

  /**
    * Ping InfluxDB
    */
  override def ping(): Future[Result] =
    readRequest(uri"$host:$port/ping", Method.GET).flatMap(toResult)

  /**
    * Close HTTP connection
    */
  override def close(): Future[Unit] = {
    backend.close()
    Future.unit
  }
}