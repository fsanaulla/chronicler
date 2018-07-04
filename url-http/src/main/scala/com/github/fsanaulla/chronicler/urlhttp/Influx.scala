package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.core.model.{InfluxConfig, InfluxCredentials}

object Influx {

  /***
    * Create HTTP client
    * @param host        - InfluxDB host
    * @param port        - InfluxDB port
    * @param credentials - user credentials
    * @return - InfluxUrlHttpClient
    */
  def connect(host: String = "localhost",
              port: Int = 8086,
              credentials: Option[InfluxCredentials] = None,
              gzipped: Boolean = false) =
    new InfluxUrlHttpClient(host, port, credentials, gzipped)

  /***
    * Create Async HTTP based client from configuration
    * @param conf    - configuration object
    * @param gzipped - enable gzip compression
    * @return        - InfluxUrlHttpClient
    */
  def connect(conf: InfluxConfig, gzipped: Boolean = false): InfluxUrlHttpClient =
    connect(conf.host, conf.port, conf.credentials, gzipped)
}
