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
  def apply(host: String = "localhost",
            port: Int = 8086,
            credentials: Option[InfluxCredentials] = None,
            gzipped: Boolean = false) =
    new InfluxUrlHttpClient(host, port, credentials, gzipped)

  /***
    * Create Async HTTP based client from configuration
    * @param conf    - configuration object
    * @return        - InfluxUrlHttpClient
    */
  def apply(conf: InfluxConfig): InfluxUrlHttpClient =
    new InfluxUrlHttpClient(conf.host, conf.port, conf.credentials, conf.gzipped)
}
