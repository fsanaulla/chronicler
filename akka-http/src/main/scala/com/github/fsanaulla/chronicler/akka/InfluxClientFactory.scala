package com.github.fsanaulla.chronicler.akka

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 31.01.18
  */
object InfluxClientFactory {

  def createHttpClient(host: String,
                       port: Int = 8086,
                       username: Option[String] = None,
                       password: Option[String] = None)
                      (implicit ex: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global, system: ActorSystem = ActorSystem()) =
    new InfluxAkkaHttpClient(host, port, username, password)

}
