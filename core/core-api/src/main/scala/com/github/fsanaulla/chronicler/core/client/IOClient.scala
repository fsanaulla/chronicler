package com.github.fsanaulla.chronicler.core.client

import com.github.fsanaulla.chronicler.core.api.{DatabaseIO, MeasurementIO}

import scala.reflect.ClassTag

/**
  * Define necessary methods for providing IO operations
  * @tparam M - Respone type container
  * @tparam E - which response entity should be user
  */
trait IOClient[M[_], E] {

  /**
    * Get database instant
    * @param dbName - database name
    * @return       - Backend related implementation of DatabaseIO
    */
  def database(dbName: String): DatabaseIO[M, E]

  /**
    * Get measurement instance with execution type A
    * @param dbName          - on which database
    * @param measurementName - which measurement
    * @tparam A              - measurement entity type
    * @return                - Backend related implementation of MeasurementIO
    */
  def measurement[A: ClassTag](dbName: String, measurementName: String): MeasurementIO[M, A, E]
}
