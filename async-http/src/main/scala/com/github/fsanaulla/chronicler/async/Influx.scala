package com.github.fsanaulla.chronicler.async

import com.github.fsanaulla.chronicler.core.model.InfluxCredentials

import scala.concurrent.ExecutionContext

object Influx {

  /***
    * Create HTTP client
    * @param host - InfluxDB host
    * @param port - InfluxDB port
    * @param credentials - user credentials
    * @param ex - Execution context
    * @return - InfluxAsyncHttpClient
    */
  def connect(host: String = "localhost",
              port: Int = 8086,
              credentials: Option[InfluxCredentials] = None)
             (implicit ex: ExecutionContext) =
    new InfluxAsyncHttpClient(host, port, credentials)
}
