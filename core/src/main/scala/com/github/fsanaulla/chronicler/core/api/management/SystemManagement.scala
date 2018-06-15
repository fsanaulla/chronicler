package com.github.fsanaulla.chronicler.core.api.management

import com.github.fsanaulla.chronicler.core.api.{DatabaseApi, MeasurementApi}
import com.github.fsanaulla.chronicler.core.model.WriteResult

import scala.reflect.ClassTag

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[chronicler] trait SystemManagement[M[_], E] {

  /** Ping InfluxDB */
  def ping: M[WriteResult]

  /**
    *
    * @param dbName - database name
    * @return Database instance that provide non type safe operations
    */
  def database(dbName: String): DatabaseApi[M, E]

  /**
    *
    * @param dbName - database name
    * @param measurementName - measurement name
    * @tparam A - Measurement's time series type
    * @return - Measurement instance of type [A]
    */
  def measurement[A: ClassTag](dbName: String, measurementName: String): MeasurementApi[M, A, E]
}
