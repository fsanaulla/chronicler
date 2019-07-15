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
import com.github.fsanaulla.chronicler.akka.shared.InfluxAkkaClient
import com.github.fsanaulla.chronicler.akka.shared.handlers.{AkkaQueryBuilder, AkkaRequestExecutor}
import com.github.fsanaulla.chronicler.akka.shared.implicits._
import com.github.fsanaulla.chronicler.core.IOClient
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.api.{DatabaseApi, MeasurementApi}
import com.github.fsanaulla.chronicler.core.components.ResponseHandler
import com.github.fsanaulla.chronicler.core.model.{InfluxCredentials, InfluxDBInfo}
import com.softwaremill.sttp.{Response, Uri}
import org.typelevel.jawn.ast.JValue

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

final class AkkaIOClient(
    host: String,
    port: Int,
    credentials: Option[InfluxCredentials],
    gzipped: Boolean,
    httpsContext: Option[HttpsConnectionContext]
  )(
    implicit ex: ExecutionContext,
    system: ActorSystem)
    extends InfluxAkkaClient(httpsContext)
    with IOClient[Future, Response[JValue], Uri, String] {

  implicit val qb: AkkaQueryBuilder                  = new AkkaQueryBuilder(host, port, credentials)
  implicit val re: AkkaRequestExecutor               = new AkkaRequestExecutor
  implicit val rh: ResponseHandler[Response[JValue]] = new ResponseHandler(jsonHandler)

  override def database(dbName: String): Database =
    new DatabaseApi(dbName, gzipped)

  override def measurement[A: ClassTag](dbName: String, measurementName: String): Measurement[A] =
    new MeasurementApi(dbName, measurementName, gzipped)

  override def ping: Future[ErrorOr[InfluxDBInfo]] = {
    re.get(qb.buildQuery("/ping", Nil))
      .map(rh.pingResult)
  }
}
