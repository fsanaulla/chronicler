package com.github.fsanaulla.api.management

import com.github.fsanaulla.api.{AkkaDatabase, AkkaMeasurement}
import com.github.fsanaulla.model.Result

import scala.concurrent.{ExecutionContext, Future}

trait SystemManagement[R] {

  implicit val ex: ExecutionContext

  /**
    *
    * @param dbName - database name
    * @return Database instance that provide non type safe operations
    */
  def database(dbName: String): AkkaDatabase

  /**
    *
    * @param dbName - database name
    * @param measurementName - measurement name
    * @tparam A - Measurement's time series type
    * @return - Measurement instance of type [A]
    */
  def measurement[A](dbName: String, measurementName: String): AkkaMeasurement[A]

  /**
    * Ping InfluxDB
    */
  def ping(): Future[Result]

  /**
    * Close HTTP connection
    */
  def close(): Future[Unit]

  /**
    * Close HTTP connection  and  shut down actor system
    */
  def closeAll(): Future[Unit]

}
