package com.github.fsanaulla.chronicler.akka

import akka.actor.ActorSystem
import com.github.fsanaulla.core.model.InfluxCredentials

import scala.concurrent.ExecutionContext

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 31.01.18
  */
object InfluxDB {

  /**
    * Create HTTP client for InfluxDB
    * @param host - hostname
    * @param port - port value
    * @param credentials - user credentials
    * @param ex - implicit execution context, by default use standard one
    * @param system - implicit actor system, by default will create new one
    * @return - InfluxAkkaHttpClient
    */
  def apply(host: String,
            port: Int = 8086,
            credentials: Option[InfluxCredentials] = None)
           (implicit ex: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global,
            system: ActorSystem = ActorSystem()) =
    new InfluxAkkaHttpClient(host, port, credentials)

}