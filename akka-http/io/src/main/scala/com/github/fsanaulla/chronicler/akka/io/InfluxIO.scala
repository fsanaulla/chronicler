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

package com.github.fsanaulla.chronicler.akka.io

import akka.actor.ActorSystem
import com.github.fsanaulla.chronicler.core.model.{InfluxConfig, InfluxCredentials}

import scala.concurrent.ExecutionContext

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
object InfluxIO {

  /**
    * Retrieve IO InfluxDB client, without management functionality
    * @param host        - hostname
    * @param port        - port value
    * @param credentials - user credentials
    * @param system      - actor system, by default will create new one
    * @param gzipped     - enable gzip compression
    * @param ex          - implicit execution context, by default use standard one
    * @return            - AkkaIOClient
    */
  def apply(host: String,
            port: Int = 8086,
            credentials: Option[InfluxCredentials] = None,
            gzipped: Boolean = false)
           (implicit ex: ExecutionContext, system: ActorSystem): AkkaIOClient =
    new AkkaIOClient(host, port, credentials, gzipped)(ex, system)

  /**
    * Retrieve IO InfluxDB client, without management functionality using configuration object
    * @param conf        - configuration object
    * @param system      - actor system, by default will create new one
    * @param ex          - implicit execution context, by default use standard one
    * @return            - AkkaIOClient
    */
  def apply(conf: InfluxConfig)
           (implicit ex: ExecutionContext, system: ActorSystem): AkkaIOClient =
    apply(conf.host, conf.port, conf.credentials, conf.gzipped)(ex, system)
}
