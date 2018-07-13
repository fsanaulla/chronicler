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

package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.core.model.{InfluxConfig, InfluxCredentials}
import com.github.fsanaulla.chronicler.urlhttp.clients.{UrlFullClient, UrlIOClient, UrlManagementClient}

object Influx {

  /**
    * Retrieve IO InfluxDB client, without management functionality
    *
    * @param host        - hostname
    * @param port        - port value
    * @param credentials - user credentials
    * @param gzipped     - enable gzip compression
    * @return            - AkkaIOClient
    */
  def io(host: String,
         port: Int = 8086,
         credentials: Option[InfluxCredentials] = None,
         gzipped: Boolean = false) =
    new UrlIOClient(host, port, credentials, gzipped)

  /**
    * Retrieve IO InfluxDB client, without management functionality using configuration object
    *
    * @param conf        - configuration object
    * @return            - UrlIOClient
    */
  def io(conf: InfluxConfig): UrlIOClient =
    io(conf.host, conf.port, conf.credentials, conf.gzipped)

  /**
    * Retrieve InfluxDB management client, without IO functionality
    *
    * @param host        - hostname
    * @param port        - port value
    * @param credentials - user credentials
    * @return            - UrlManagementClient
    */
  def management(host: String,
                 port: Int = 8086,
                 credentials: Option[InfluxCredentials] = None) =
    new UrlManagementClient(host, port, credentials)

  /**
    * Retrieve InfluxDB management client, without IO functionality
    * @param conf        - configuration object
    * @return            - UrlManagementClient
    */
  def management(conf: InfluxConfig) =
    new UrlManagementClient(conf.host, conf.port, conf.credentials)

  /***
    * Create HTTP client
    * @param host        - InfluxDB host
    * @param port        - InfluxDB port
    * @param credentials - user credentials
    * @return            - UrlHttpClient
    */
  def full(host: String,
           port: Int = 8086,
           credentials: Option[InfluxCredentials] = None,
           gzipped: Boolean = false) =
    new UrlFullClient(host, port, credentials, gzipped)

  /***
    * Create Url HTTP based client from configuration
    * @param conf    - configuration object
    * @return        - InfluxUrlHttpClient
    */
  def full(conf: InfluxConfig): UrlFullClient =
    full(conf.host, conf.port, conf.credentials, conf.gzipped)
}
