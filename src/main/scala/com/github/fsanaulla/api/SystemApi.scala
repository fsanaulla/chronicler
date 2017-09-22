package com.github.fsanaulla.api

import akka.actor.Terminated
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import com.github.fsanaulla.clients.InfluxHttpClient
import com.github.fsanaulla.db.Database
import com.github.fsanaulla.model.Result
import com.github.fsanaulla.utils.ResponseHandler.toResult

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 03.09.17
  */
private[fsanaulla] trait SystemApi {
  self: InfluxHttpClient =>

  def use(dbName: String): Database = {
    new Database(dbName)
  }

  def ping(): Future[Result] = {
    buildRequest("/ping", GET).flatMap(toResult)
  }

  def close(): Future[Terminated] = {
    Http().shutdownAllConnectionPools()
    system.terminate()
  }
}
