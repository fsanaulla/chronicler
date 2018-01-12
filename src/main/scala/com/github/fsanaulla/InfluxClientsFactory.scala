package com.github.fsanaulla

import akka.actor.ActorSystem
import com.github.fsanaulla.clients.{InfluxAkkaHttpClient, InfluxUdpClient}

import scala.concurrent.ExecutionContext

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
object InfluxClientsFactory {

  def createHttpClient(host: String,
                       port: Int = 8086,
                       username: Option[String] = None,
                       password: Option[String] = None)
                      (implicit ex: ExecutionContext = ExecutionContext.Implicits.global,
                       system: ActorSystem = ActorSystem("chronicler-actorsystem")): InfluxAkkaHttpClient = {

    new InfluxAkkaHttpClient(host, port, username, password)
  }

  def createUdpClient(host: String, port: Int = 8089): InfluxUdpClient = {
    new InfluxUdpClient(host, port)
  }
}
