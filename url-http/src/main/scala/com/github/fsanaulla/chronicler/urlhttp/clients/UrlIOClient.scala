package com.github.fsanaulla.chronicler.urlhttp.clients

import com.github.fsanaulla.chronicler.core.client.IOClient
import com.github.fsanaulla.chronicler.core.model.InfluxCredentials
import com.github.fsanaulla.chronicler.urlhttp.api.{Database, Measurement}
import com.softwaremill.sttp.{SttpBackend, TryHttpURLConnectionBackend}

import scala.reflect.ClassTag
import scala.util.Try

class UrlIOClient(val host: String,
                  val port: Int,
                  val credentials: Option[InfluxCredentials],
                  gzipped: Boolean)
  extends IOClient[Try, String] with AutoCloseable {

  protected implicit val backend: SttpBackend[Try, Nothing] =
    TryHttpURLConnectionBackend()

  override def database(dbName: String): Database =
    new Database(host, port, credentials, dbName, gzipped)

  override def measurement[A: ClassTag](dbName: String,
                                        measurementName: String): Measurement[A] =
    new Measurement[A](host, port, credentials, dbName, measurementName, gzipped)

  override def close(): Unit = backend.close()
}
