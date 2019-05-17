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

package com.github.fsanaulla.chronicler.urlhttp.management

import com.github.fsanaulla.chronicler.core.ManagementClient
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.components.ResponseHandler
import com.github.fsanaulla.chronicler.core.model.{Functor, InfluxCredentials, InfluxDBInfo}
import com.github.fsanaulla.chronicler.urlhttp.shared.InfluxUrlClient
import com.github.fsanaulla.chronicler.urlhttp.shared.InfluxUrlClient.CustomizationF
import com.github.fsanaulla.chronicler.urlhttp.shared.alias.Request
import com.github.fsanaulla.chronicler.urlhttp.shared.handlers.{UrlQueryBuilder, UrlRequestExecutor}
import com.github.fsanaulla.chronicler.urlhttp.shared.implicits.jsonHandler
import com.softwaremill.sttp.{Response, Uri}
import jawn.ast.JValue

import scala.util.Try


final class UrlManagementClient(host: String,
                                port: Int,
                                credentials: Option[InfluxCredentials],
                                customization: Option[CustomizationF])
                               (implicit val F: Functor[Try])
  extends InfluxUrlClient(customization) with ManagementClient[Try, Request, Response[JValue], Uri, String] {

  implicit val qb: UrlQueryBuilder = new UrlQueryBuilder(host, port, credentials)
  implicit val re: UrlRequestExecutor = new UrlRequestExecutor
  implicit val rh: ResponseHandler[Response[JValue]] = new ResponseHandler(jsonHandler)

  override def ping: Try[ErrorOr[InfluxDBInfo]] = {
    re
      .executeUri(qb.buildQuery("/ping", Map.empty[String, String]))
      .map(rh.pingResult)
  }
}
