package com.github.fsanaulla.chronicler.akka

import akka.actor.ActorSystem
import com.github.fsanaulla.chronicler.akka.clients.{AkkaFullClient, AkkaIOClient, AkkaManagementClient}
import com.github.fsanaulla.chronicler.core.model.{InfluxConfig, InfluxCredentials}

import scala.concurrent.ExecutionContext

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
object Influx {

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
  def io(host: String,
         port: Int = 8086,
         credentials: Option[InfluxCredentials] = None,
         gzipped: Boolean = false)
        (implicit ex: ExecutionContext, system: ActorSystem) =
    new AkkaIOClient(host, port, credentials, gzipped)(ex, system)

  /**
    * Retrieve IO InfluxDB client, without management functionality using configuration object
    * @param conf        - configuration object
    * @param system      - actor system, by default will create new one
    * @param ex          - implicit execution context, by default use standard one
    * @return            - AkkaIOClient
    */
  def io(conf: InfluxConfig)
        (implicit ex: ExecutionContext, system: ActorSystem): AkkaIOClient =
    io(conf.host, conf.port, conf.credentials, conf.gzipped)(ex, system)

  /**
    * Retrieve InfluxDB management client, without IO functionality
    * @param host        - hostname
    * @param port        - port value
    * @param credentials - user credentials
    * @param system      - actor system, by default will create new one
    * @param ex          - implicit execution context, by default use standard one
    * @return            - AkkaManagementClient
    */
  def management(host: String,
                 port: Int = 8086,
                 credentials: Option[InfluxCredentials] = None)
                (implicit ex: ExecutionContext, system: ActorSystem) =
    new AkkaManagementClient(host, port, credentials)(ex, system)

  /**
    * Retrieve InfluxDB management client, without IO functionality
    * @param conf        - configuration object
    * @param system      - actor system, by default will create new one
    * @param ex          - implicit execution context, by default use standard one
    * @return            - AkkaManagementClient
    */
  def management(conf: InfluxConfig)
                (implicit ex: ExecutionContext, system: ActorSystem) =
    new AkkaManagementClient(conf.host, conf.port, conf.credentials)(ex, system)

  /**
    * Retrieve fully functional Akka InfluxDB client
    * @param host        - hostname
    * @param port        - port value
    * @param credentials - user credentials
    * @param system      - actor system, by default will create new one
    * @param gzipped     - enable gzip compression
    * @param ex          - implicit execution context, by default use standard one
    * @return            - InfluxAkkaHttpClient
    */
  def full(host: String,
            port: Int = 8086,
            credentials: Option[InfluxCredentials] = None,
            gzipped: Boolean = false)
           (implicit ex: ExecutionContext, system: ActorSystem) =
    new AkkaFullClient(host, port, credentials, gzipped)(ex, system)

  /**
    * Retrieve fully functional Akka InfluxDB client using config
    * @param conf        - configuration object
    * @param system      - actor system, by default will create new one
    * @param ex          - implicit execution context, by default use standard one
    * @return            - InfluxAkkaHttpClient
    */
  def full(conf: InfluxConfig)(implicit ex: ExecutionContext, system: ActorSystem): AkkaFullClient =
    full(conf.host, conf.port, conf.credentials, conf.gzipped)(ex, system)
}
