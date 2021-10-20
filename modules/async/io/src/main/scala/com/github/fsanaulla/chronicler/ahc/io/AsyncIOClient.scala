/*
 * Copyright 2017-2019 Faiaz Sanaulla
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

package com.github.fsanaulla.chronicler.async.io

import com.github.fsanaulla.chronicler.async.shared.{
  AsyncQueryBuilder,
  AsyncRequestBuilder,
  AsyncRequestExecutor,
  AsyncJsonHandler,
  RequestE,
  ResponseE,
  fkId,
  futureFunctor,
  futureMonadError
}
import com.github.fsanaulla.chronicler.core.IOClient
import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, Id}
import com.github.fsanaulla.chronicler.core.api.{DatabaseApi, MeasurementApi}
import com.github.fsanaulla.chronicler.core.auth.InfluxCredentials
import com.github.fsanaulla.chronicler.core.components.ResponseHandlerBase
import com.github.fsanaulla.chronicler.core.model.InfluxDBInfo
import com.github.fsanaulla.chronicler.core.typeclasses.Apply._
import org.asynchttpclient.AsyncHttpClientConfig
import sttp.client3.Identity
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend
import sttp.model.Uri

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.reflect.ClassTag

final class AsyncIOClient(
    host: String,
    port: Int,
    compress: Boolean,
    credentials: Option[InfluxCredentials],
    asyncClientConfig: Option[AsyncHttpClientConfig]
)(implicit ec: ExecutionContext)
    extends IOClient[Future, Id, RequestE[Identity], Uri, String, ResponseE] {

  private val backend = asyncClientConfig.fold(AsyncHttpClientFutureBackend())(
    c => AsyncHttpClientFutureBackend.usingConfig(c)
  )

  implicit val qb: AsyncQueryBuilder    = new AsyncQueryBuilder(host, port)
  implicit val rb: AsyncRequestBuilder  = new AsyncRequestBuilder(credentials)
  implicit val re: AsyncRequestExecutor = new AsyncRequestExecutor(backend)
  implicit val rh: ResponseHandlerBase[Id, ResponseE] = new ResponseHandlerBase(
    new AsyncJsonHandler
  )

  override def database(dbName: String) =
    new DatabaseApi(dbName, compress)

  override def measurement[A: ClassTag](dbName: String, measurementName: String): Measurement[A] =
    new MeasurementApi(dbName, measurementName, compress)

  override def ping: Future[ErrorOr[InfluxDBInfo]] = {
    val uri  = qb.buildQuery("/ping")
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    resp.map(rh.pingResult)
  }

  override def close(): Unit =
    Await.ready(closeAsync(), Duration.Inf)

  def closeAsync(): Future[Unit] =
    backend.close()
}
