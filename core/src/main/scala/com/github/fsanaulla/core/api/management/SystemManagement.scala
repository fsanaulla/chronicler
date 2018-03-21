package com.github.fsanaulla.core.api.management

import com.github.fsanaulla.core.api.{DatabaseApi, MeasurementApi}
import com.github.fsanaulla.core.model.{Executable, Result}

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
trait SystemManagement[E] { self: Executable =>

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
