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

package com.github.fsanaulla.chronicler.ahc.io

import com.github.fsanaulla.chronicler.ahc.shared.InfluxAhcClient
import com.github.fsanaulla.chronicler.ahc.shared.handlers.{AhcQueryBuilder, AhcRequestExecutor}
import com.github.fsanaulla.chronicler.ahc.shared.implicits._
import com.github.fsanaulla.chronicler.core.IOClient
import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, Id}
import com.github.fsanaulla.chronicler.core.api.{DatabaseApi, MeasurementApi}
import com.github.fsanaulla.chronicler.core.components.ResponseHandler
import com.github.fsanaulla.chronicler.core.implicits.{applyId, functorId}
import com.github.fsanaulla.chronicler.core.model.{InfluxCredentials, InfluxDBInfo}
import com.softwaremill.sttp.{Response, Uri}
import org.asynchttpclient.AsyncHttpClientConfig
import org.typelevel.jawn.ast.JValue

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

final class AhcIOClient(
    host: String,
    port: Int,
    gzipped: Boolean,
    credentials: Option[InfluxCredentials],
    asyncClientConfig: Option[AsyncHttpClientConfig]
  )(implicit ex: ExecutionContext)
  extends InfluxAhcClient(asyncClientConfig)
  with IOClient[Future, Id, Response[JValue], Uri, String] {

  implicit val qb: AhcQueryBuilder                       = new AhcQueryBuilder(host, port, credentials)
  implicit val re: AhcRequestExecutor                    = new AhcRequestExecutor
  implicit val rh: ResponseHandler[Id, Response[JValue]] = new ResponseHandler(jsonHandler)

  override def database(dbName: String) =
    new DatabaseApi(dbName, gzipped)

  override def measurement[A: ClassTag](dbName: String, measurementName: String): Measurement[A] =
    new MeasurementApi(dbName, measurementName, gzipped)

  override def ping: Future[ErrorOr[InfluxDBInfo]] =
    re.get(qb.buildQuery("/ping"))
      .map(rh.pingResult)

}
