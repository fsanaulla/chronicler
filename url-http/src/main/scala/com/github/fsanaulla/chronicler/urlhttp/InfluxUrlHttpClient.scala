package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.urlhttp.api.{Database, Measurement}
import com.github.fsanaulla.chronicler.urlhttp.handlers.{UrlQueryHandler, UrlRequestHandler, UrlResponseHandler}
import com.github.fsanaulla.core.client.InfluxClient
import com.github.fsanaulla.core.model.{InfluxCredentials, Mapper, Result}
import com.softwaremill.sttp.{Response, SttpBackend, TryHttpURLConnectionBackend, Uri}
import jawn.ast.JValue

import scala.reflect.ClassTag
import scala.util.Try

final class InfluxUrlHttpClient(
                                 val host: String,
                                 val port: Int,
                                 val credentials: Option[InfluxCredentials])
  extends InfluxClient[Try, Response[JValue], Uri, String]
    with UrlRequestHandler
    with UrlResponseHandler
    with UrlQueryHandler {

  override def m: Mapper[Try, Response[JValue]] = new Mapper[Try, Response[JValue]] {
    override def mapTo[B](resp: Try[Response[JValue]], f: Response[JValue] => Try[B]): Try[B] = resp.flatMap(f)
  }

  protected implicit val backend: SttpBackend[Try, Nothing] = TryHttpURLConnectionBackend()

  /**
    * Select database
    * @param dbName - database name
    * @return Database instance that provide non type safe operations
    */
  override def database(dbName: String): Database =
    new Database(host, port, credentials, dbName)

  /**
    *
    * @param dbName          - database name
    * @param measurementName - measurement name
    * @tparam A - Measurement's time series type
    * @return - Measurement instance of type [A]
    */
  override def measurement[A: ClassTag](dbName: String, measurementName: String): Measurement[A] =
    new Measurement[A](host, port, credentials, dbName, measurementName)

  /**
    * Ping InfluxDB
    */
  override def ping: Try[Result] =
    readRequest(buildQuery("/ping", Map.empty[String, String])).flatMap(toResult)

  override def close(): Unit = backend.close()
}
