package com.github.fsanaulla.core.api.management

import com.github.fsanaulla.core.api.{DatabaseApi, MeasurementApi}
import com.github.fsanaulla.core.model.Result

import scala.concurrent.{ExecutionContext, Future}

trait SystemManagement[E] {

  protected implicit val ex: ExecutionContext

  /**
    *
    * @param dbName - database name
    * @return Database instance that provide non type safe operations
    */
  def database(dbName: String): DatabaseApi[E]

  /**
    *
    * @param dbName - database name
    * @param measurementName - measurement name
    * @tparam A - Measurement's time series type
    * @return - Measurement instance of type [A]
    */
  def measurement[A](dbName: String, measurementName: String): MeasurementApi[A, E]

  /**
    * Ping InfluxDB
    */
  def ping(): Future[Result]
}
