package com.github.fsanaulla.chronicler.async.clients

import com.github.fsanaulla.chronicler.async.api.{Database, Measurement}
import com.github.fsanaulla.chronicler.core.client.IOClient
import com.github.fsanaulla.chronicler.core.model.InfluxCredentials
import com.softwaremill.sttp.SttpBackend
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

final class AsyncIOClient(val host: String,
                          val port: Int,
                          val credentials: Option[InfluxCredentials],
                          gzipped: Boolean)(implicit val ex: ExecutionContext)
  extends IOClient[Future, String] with AutoCloseable {

  protected implicit val backend: SttpBackend[Future, Nothing] = AsyncHttpClientFutureBackend()

  override def database(dbName: String): Database =
    new Database(host, port, credentials, dbName, gzipped)

  override def measurement[A: ClassTag](dbName: String,
                                        measurementName: String): Measurement[A] =
    new Measurement[A](host, port, credentials, dbName, measurementName, gzipped)

  override def close(): Unit =
    backend.close()
}
