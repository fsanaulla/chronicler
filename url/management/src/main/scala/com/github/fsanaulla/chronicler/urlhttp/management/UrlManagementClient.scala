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

package com.github.fsanaulla.chronicler.urlhttp.management

import com.github.fsanaulla.chronicler.core.ManagementClient
import com.github.fsanaulla.chronicler.core.model.{InfluxCredentials, WriteResult}
import com.github.fsanaulla.chronicler.core.typeclasses.FlatMap
import com.github.fsanaulla.chronicler.urlhttp.shared.InfluxUrlClient
import com.github.fsanaulla.chronicler.urlhttp.shared.InfluxUrlClient.CustomizationF
import com.github.fsanaulla.chronicler.urlhttp.shared.alias.Request
import com.github.fsanaulla.chronicler.urlhttp.shared.handlers.{UrlQueryBuilder, UrlRequestExecutor, UrlResponseHandler}
import com.softwaremill.sttp.{Response, Uri}
import jawn.ast.JValue

import scala.util.Try

final class UrlManagementClient(val host: String,
                                val port: Int,
                                val credentials: Option[InfluxCredentials],
                                customization: Option[CustomizationF])
  extends InfluxUrlClient(customization)
    with ManagementClient[Try, Request, Response[JValue], Uri, String]
    with UrlRequestExecutor
    with UrlResponseHandler
    with UrlQueryBuilder
    with FlatMap[Try]
    with AutoCloseable {

  private[chronicler] override def flatMap[A, B](fa: Try[A])(f: A => Try[B]): Try[B] = fa.flatMap(f)

  override def ping: Try[WriteResult] =
    execute(buildQuery("/ping", Map.empty[String, String])).flatMap(toResult)
}
