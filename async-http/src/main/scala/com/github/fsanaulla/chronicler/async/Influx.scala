package com.github.fsanaulla.chronicler.async

import com.github.fsanaulla.chronicler.core.model.{InfluxConfig, InfluxCredentials}

import scala.concurrent.ExecutionContext

object Influx {

  /***
    * Create Async HTTP based client
    * @param host        - InfluxDB host
    * @param port        - InfluxDB port
    * @param credentials - user credentials
    * @param ex          - Execution context
    * @return            - InfluxAsyncHttpClient
    */
  def apply(host: String = "localhost",
            port: Int = 8086,
            credentials: Option[InfluxCredentials] = None,
            gzipped: Boolean = false)
           (implicit ex: ExecutionContext) =
    new InfluxAsyncHttpClient(host, port, credentials, gzipped)

  /***
    * Create Async HTTP based client from configuration
    * @param conf    - configuration object
    * @param ex      - Execution context
    * @return        - InfluxAsyncHttpClient
    */
  def apply(conf: InfluxConfig)(implicit ex: ExecutionContext): InfluxAsyncHttpClient =
    new InfluxAsyncHttpClient(conf.host, conf.port, conf.credentials, conf.gzipped)
}
