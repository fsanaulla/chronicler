package com.github.fsanaulla.api

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import com.github.fsanaulla.clients.InfluxHttpClient
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

  /**
    *
    * @param dbName - database name
    * @return Database instance that provide non type safe operations
    */
  def database(dbName: String): Database = new Database(dbName)

  /**
    *
    * @param dbName - database name
    * @param measurementName - measurement name
    * @tparam A - Measurement's time series type
    * @return - Measurement instance of type [A]
    */
  def measurement[A](dbName: String, measurementName: String): Measurement[A] = {
    new Measurement[A](dbName, measurementName)
  }

  /**
    * Ping InfluxDB
    */
  def ping(): Future[Result] = {
    buildRequest("/ping", GET).flatMap(toResult)
  }

  /**
    * Close HTTP connection
    */
  def close(): Future[Unit] = Http().shutdownAllConnectionPools()

  /**
    * Close HTTP connection  and  shut down actor system
    */
  def closeAll(): Future[Unit] = for {
      _ <- Http().shutdownAllConnectionPools()
      _ <- system.terminate()
  } yield ()
}
