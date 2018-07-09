package com.github.fsanaulla.chronicler.async

import com.github.fsanaulla.chronicler.async.clients.{AsyncFullClient, AsyncIOClient, AsyncManagementClient}
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

  /**
    * Retrieve InfluxDB management client, without IO functionality
    * @param host        - hostname
    * @param port        - port value
    * @param credentials - user credentials
    * @param ex          - implicit execution context, by default use standard one
    * @return            - AsyncManagementClient
    */
  def management(host: String,
                 port: Int = 8086,
                 credentials: Option[InfluxCredentials] = None)
                (implicit ex: ExecutionContext) =
    new AsyncManagementClient(host, port, credentials)

  /**
    * Retrieve InfluxDB management client, without IO functionality
    * @param conf        - configuration object
    * @param ex          - implicit execution context, by default use standard one
    * @return            - AsyncManagementClient
    */
  def management(conf: InfluxConfig)
                (implicit ex: ExecutionContext) =
    new AsyncManagementClient(conf.host, conf.port, conf.credentials)

  /**
    * Retrieve fully functional Async InfluxDB client
    * @param host        - hostname
    * @param port        - port value
    * @param credentials - user credentials
    * @param gzipped     - enable gzip compression
    * @param ex          - implicit execution context, by default use standard one
    * @return            - InfluxAsyncHttpClient
    */
  def full(host: String,
           port: Int = 8086,
           credentials: Option[InfluxCredentials] = None,
           gzipped: Boolean = false)
          (implicit ex: ExecutionContext) =
    new AsyncFullClient(host, port, credentials, gzipped)

  /**
    * Retrieve fully functional Async InfluxDB client using config
    * @param conf        - configuration object
    * @param ex          - implicit execution context, by default use standard one
    * @return            - InfluxAsyncHttpClient
    */
  def full(conf: InfluxConfig)(implicit ex: ExecutionContext): AsyncFullClient =
    full(conf.host, conf.port, conf.credentials, conf.gzipped)
}
