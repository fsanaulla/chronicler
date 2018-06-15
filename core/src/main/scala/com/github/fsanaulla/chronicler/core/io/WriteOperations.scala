package com.github.fsanaulla.chronicler.core.io

import com.github.fsanaulla.chronicler.core.enums.{Consistency, Precision}
import com.github.fsanaulla.chronicler.core.model.WriteResult

/***
  * Define basic write operation for communicating with InfluxDB
  * @tparam M - container type
  * @tparam E - Entity type
  */
trait WriteOperations[M[_], E] {

  /**
    * Execute single write to InfluxDB
    * @param dbName          - For which database
    * @param entity          - Entity that should be entered
    * @param consistency     - Consistency level
    * @param precision       - Precision level
    * @param retentionPolicy - Optional retention policy name
    * @return                - Result of execution
    */
  def writeTo(
              dbName: String,
              entity: E,
              consistency: Consistency,
              precision: Precision,
              retentionPolicy: Option[String]): M[WriteResult]

}
