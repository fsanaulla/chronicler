package com.github.fsanaulla.chronicler.async

import com.github.fsanaulla.chronicler.async.api.{Database, Measurement}
import com.github.fsanaulla.chronicler.async.handlers._
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend
import com.softwaremill.sttp.{Response, SttpBackend, Uri}
import jawn.ast.JValue

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

final class InfluxAsyncHttpClient(val host: String,
                                  val port: Int,
                                  val credentials: Option[InfluxCredentials])(implicit val ex: ExecutionContext)
  extends InfluxClient[Future, Response[JValue], Uri, String]
    with AsyncRequestHandler
    with AsyncResponseHandler
    with AsyncQueryHandler {

  protected implicit val backend: SttpBackend[Future, Nothing] = AsyncHttpClientFutureBackend()

  override val m: Mapper[Future, Response[JValue]] = new Mapper[Future, Response[JValue]] {
    override def mapTo[B](resp: Future[Response[JValue]], f: Response[JValue] => Future[B]): Future[B] = resp.flatMap(f)
  }

  /**
    *
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
  override def ping(): Future[WriteResult] =
    readRequest(buildQuery("/ping", Map.empty[String, String])).flatMap(toResult)

  /**
    * Close HTTP connection
    */
  override def close(): Unit = backend.close()

}
