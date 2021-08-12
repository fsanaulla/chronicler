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

package com.github.fsanaulla.chronicler.urlhttp.io

import com.github.fsanaulla.chronicler.core.IOClient
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.api.{DatabaseApi, MeasurementApi}
import com.github.fsanaulla.chronicler.core.components.ResponseHandler
import com.github.fsanaulla.chronicler.core.model.{InfluxCredentials, InfluxDBInfo}
import com.github.fsanaulla.chronicler.urlhttp.shared.{ResponseE, UrlJsonHandler, UrlQueryBuilder, UrlRequestExecutor, tryApply, tryFailable, tryFunctor}
import sttp.client3.{SttpBackend, TryHttpURLConnectionBackend}
import sttp.model.Uri

import scala.reflect.ClassTag
import scala.util.Try

final class UrlIOClient(
    host: String,
    port: Int,
    credentials: Option[InfluxCredentials],
    compress: Boolean
) extends IOClient[Try, Try, ResponseE, Uri, String] {

  private val backend: SttpBackend[Try, Any]       = TryHttpURLConnectionBackend()
  implicit val qb: UrlQueryBuilder                 = new UrlQueryBuilder(host, port, credentials)
  implicit val re: UrlRequestExecutor              = new UrlRequestExecutor(backend)
  implicit val rh: ResponseHandler[Try, ResponseE] = new ResponseHandler(UrlJsonHandler)

  override def database(dbName: String): Database =
    new DatabaseApi(dbName, compress)

  override def measurement[A: ClassTag](
      dbName: String,
      measurementName: String
  ): Measurement[A] =
    new MeasurementApi(dbName, measurementName, compress)

  override def ping: Try[ErrorOr[InfluxDBInfo]] = {
    re.get(qb.buildQuery("/ping"), compression = false)
      .flatMap(rh.pingResult)
  }

  override def close(): Unit = {
    backend.close()
    ()
  }
}
