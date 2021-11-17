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

package com.github.fsanaulla.chronicler.akka.io

import akka.actor.ActorSystem
import akka.http.scaladsl.HttpsConnectionContext
import com.github.fsanaulla.chronicler.akka.shared.{
  AkkaJsonHandler,
  AkkaQueryBuilder,
  AkkaRequestBuilder,
  AkkaRequestExecutor,
  RequestE,
  ResponseE,
  futureApply,
  futureFunctor,
  futureMonadError
}
import com.github.fsanaulla.chronicler.core.IOClient
import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, Id}
import com.github.fsanaulla.chronicler.core.api.MeasurementApi
import com.github.fsanaulla.chronicler.core.auth.InfluxCredentials
import com.github.fsanaulla.chronicler.core.model.InfluxDBInfo
import sttp.capabilities
import sttp.capabilities.akka.AkkaStreams
import sttp.client3.akkahttp.AkkaHttpBackend
import sttp.client3.{Identity, SttpBackend}
import sttp.model.Uri

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.reflect.ClassTag

final class AkkaIOClient(
    host: String,
    port: Int,
    credentials: Option[InfluxCredentials],
    compress: Boolean,
    httpsContext: Option[HttpsConnectionContext]
)(implicit ec: ExecutionContext, system: ActorSystem)
    extends IOClient[Future, Id, RequestE[Identity], Uri, String, ResponseE] {

  private val backend: SttpBackend[Future, AkkaStreams with capabilities.WebSockets] =
    AkkaHttpBackend.usingActorSystem(system, customHttpsContext = httpsContext)

  implicit val rb: AkkaRequestBuilder  = new AkkaRequestBuilder(credentials)
  implicit val qb: AkkaQueryBuilder    = new AkkaQueryBuilder(host, port)
  implicit val re: AkkaRequestExecutor = new AkkaRequestExecutor(backend)
  implicit val rh: AkkaResponseHandler = new AkkaResponseHandler(new AkkaJsonHandler)

  override def database(dbName: String) =
    new AkkaDatabaseApi(dbName, compress)

  override def measurement[A: ClassTag](
      dbName: String,
      measurementName: String
  ) =
    new AkkaMeasurementApi[A](dbName, measurementName, compress)

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
