package com.github.fsanaulla

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext

object InfluxClientFactory {
  def createHttpClient(host: String,
                       port: Int = 8086,
                       username: Option[String] = None,
                       password: Option[String] = None)
                      (implicit ex: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global, system: ActorSystem = ActorSystem()) =
    new InfluxAsyncHttpClient(host, port, username, password)

}
