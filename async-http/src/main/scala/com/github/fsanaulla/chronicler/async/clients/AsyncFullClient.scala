/*
 * Copyright 2017-2018 Faiaz Sanaulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fsanaulla.chronicler.async.clients

import com.github.fsanaulla.chronicler.async.api.{Database, Measurement}
import com.github.fsanaulla.chronicler.async.handlers.{AsyncQueryHandler, AsyncRequestHandler, AsyncResponseHandler}
import com.github.fsanaulla.chronicler.async.utils.Aliases.Request
import com.github.fsanaulla.chronicler.core.client.FullClient
import com.github.fsanaulla.chronicler.core.model.{InfluxCredentials, Mappable, WriteResult}
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend
import com.softwaremill.sttp.{Response, SttpBackend, Uri}
import jawn.ast.JValue

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

final class AsyncFullClient(val host: String,
                            val port: Int,
                            val credentials: Option[InfluxCredentials],
                            gzipped: Boolean)(implicit val ex: ExecutionContext)
  extends FullClient[Future, Request, Response[JValue], Uri, String]
    with AsyncRequestHandler
    with AsyncResponseHandler
    with AsyncQueryHandler
    with Mappable[Future, Response[JValue]]
    with AutoCloseable {

  private[async] implicit val backend: SttpBackend[Future, Nothing] =
    AsyncHttpClientFutureBackend()

  private[chronicler] override def mapTo[B](resp: Future[Response[JValue]],
                                            f: Response[JValue] => Future[B]): Future[B] = resp.flatMap(f)

  override def database(dbName: String): Database =
    new Database(host, port, credentials, dbName, gzipped)

  override def measurement[A: ClassTag](dbName: String,
                                        measurementName: String): Measurement[A] =
    new Measurement[A](host, port, credentials, dbName, measurementName, gzipped)

  /** Ping InfluxDB */
  override def ping: Future[WriteResult] =
    execute(buildQuery("/ping", Map.empty[String, String])).flatMap(toResult)

  /** Close HTTP connection */
  override def close(): Unit = backend.close()

}
