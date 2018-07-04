package com.github.fsanaulla.chronicler.akka

import akka.actor.ActorSystem
import com.github.fsanaulla.chronicler.core.model.{InfluxConfig, InfluxCredentials}

import scala.concurrent.ExecutionContext

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
object Influx {

  /**
    * Create HTTP client for InfluxDB
    * @param host        - hostname
    * @param port        - port value
    * @param credentials - user credentials
    * @param system      - actor system, by default will create new one
    * @param gzipped     - enable gzip compression
    * @param ex          - implicit execution context, by default use standard one
    * @return            - InfluxAkkaHttpClient
    */
  def connect(host: String = "localhost",
              port: Int = 8086,
              credentials: Option[InfluxCredentials] = None,
              system: ActorSystem = ActorSystem(),
              gzipped: Boolean = false)
             (implicit ex: ExecutionContext) =
    new InfluxAkkaHttpClient(host, port, credentials, gzipped)(ex, system)

  /**
    * Create Akka HTTP based influxdb client from configuration object
    * @param conf        - configuration object
    * @param system      - actor system, by default will create new one
    * @param gzipped     - enable gzip compression
    * @param ex          - implicit execution context, by default use standard one
    * @return            - InfluxAkkaHttpClient
    */
  def connect(conf: InfluxConfig,
              system: ActorSystem = ActorSystem(),
              gzipped: Boolean)(implicit ex: ExecutionContext): InfluxAkkaHttpClient =
    connect(conf.host, conf.port, conf.credentials, system, gzipped)
}
