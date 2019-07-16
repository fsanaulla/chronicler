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
import com.github.fsanaulla.chronicler.core.components.ResponseHandler
import com.github.fsanaulla.chronicler.core.model.{InfluxCredentials, InfluxDBInfo}
import com.github.fsanaulla.chronicler.urlhttp.shared.Url
import com.github.fsanaulla.chronicler.urlhttp.shared.handlers.{UrlQueryBuilder, UrlRequestExecutor}
import com.github.fsanaulla.chronicler.urlhttp.shared.implicits._
import requests.Response

import scala.reflect.ClassTag
import scala.util.Try

final class UrlIOClient(
    host: String,
    port: Int,
    credentials: Option[InfluxCredentials],
    gzipped: Boolean,
    ssl: Boolean)
  extends IOClient[Try, Response, Url, String] {

  implicit val qb: UrlQueryBuilder           = new UrlQueryBuilder(host, port, credentials, ssl)
  implicit val re: UrlRequestExecutor        = new UrlRequestExecutor(ssl, jsonHandler)
  implicit val rh: ResponseHandler[Response] = new ResponseHandler(jsonHandler)

  override def database(dbName: String): UrlDatabaseApi =
    new UrlDatabaseApi(dbName, gzipped)

  override def measurement[A: ClassTag](
      dbName: String,
      measurementName: String
    ): UrlMeasurementApi[A] =
    new UrlMeasurementApi(dbName, measurementName, gzipped)

  override def ping: Try[ErrorOr[InfluxDBInfo]] = {
    re.get(qb.buildQuery("/ping"))
      .map(rh.pingResult)
  }

  override def close(): Unit = {}
}
