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

package com.github.fsanaulla.chronicler.urlhttp.clients

import com.github.fsanaulla.chronicler.core.client.IOClient
import com.github.fsanaulla.chronicler.core.model.InfluxCredentials
import com.github.fsanaulla.chronicler.urlhttp.api.{Database, Measurement}
import com.softwaremill.sttp.{SttpBackend, TryHttpURLConnectionBackend}

import scala.reflect.ClassTag
import scala.util.Try

final class UrlIOClient(val host: String,
                        val port: Int,
                        val credentials: Option[InfluxCredentials],
                        gzipped: Boolean)
  extends IOClient[Try, String] {

  private[urlhttp] implicit val backend: SttpBackend[Try, Nothing] =
    TryHttpURLConnectionBackend()

  override def database(dbName: String): Database =
    new Database(host, port, credentials, dbName, gzipped)

  override def measurement[A: ClassTag](dbName: String,
                                        measurementName: String): Measurement[A] =
    new Measurement[A](host, port, credentials, dbName, measurementName, gzipped)

  override def close(): Unit = backend.close()
}
