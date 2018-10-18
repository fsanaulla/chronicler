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

package com.github.fsanaulla.chronicler.async.io

import com.github.fsanaulla.chronicler.core.model.{InfluxConfig, InfluxCredentials}

import scala.concurrent.ExecutionContext

object Influx {

  /**
    * Retrieve IO InfluxDB client, without management functionality
    * @param host        - hostname
    * @param port        - port value
    * @param credentials - user credentials
    * @param gzipped     - enable gzip compression
    * @param ex          - implicit execution context, by default use standard one
    * @return            - AkkaIOClient
    */
  def io(host: String,
         port: Int = 8086,
         credentials: Option[InfluxCredentials] = None,
         gzipped: Boolean = false)
        (implicit ex: ExecutionContext) =
    new AsyncIOClient(host, port, credentials, gzipped)

  /**
    * Retrieve IO InfluxDB client, without management functionality using configuration object
    * @param conf        - configuration object
    * @param ex          - implicit execution context, by default use standard one
    * @return            - AsyncIOClient
    */
  def io(conf: InfluxConfig)
        (implicit ex: ExecutionContext): AsyncIOClient =
    io(conf.host, conf.port, conf.credentials, conf.gzipped)
}
