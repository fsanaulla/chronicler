package com.github.fsanaulla.chronicler.async

import com.github.fsanaulla.core.model.InfluxCredentials

import scala.concurrent.ExecutionContext

object InfluxDB {

  def apply(host: String,
            port: Int = 8086,
            credentials: Option[InfluxCredentials] = None)
           (implicit ex: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global) =
    new InfluxAsyncHttpClient(host, port, credentials)
}
