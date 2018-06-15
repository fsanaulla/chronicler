package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.core.model.InfluxCredentials

object Influx {

  /***
    * Create HTTP client
    * @param host - InfluxDB host
    * @param port - InfluxDB port
    * @param credentials - user credentials
    * @return - InfluxAsyncHttpClient
    */
  def connect(host: String = "localhost",
              port: Int = 8086,
              credentials: Option[InfluxCredentials] = None) =
    new InfluxUrlHttpClient(host, port, credentials)
}
