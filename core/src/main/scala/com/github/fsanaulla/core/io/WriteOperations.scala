package com.github.fsanaulla.core.io

import com.github.fsanaulla.core.enums.{Consistency, Precision}
import com.github.fsanaulla.core.model.Result

import scala.concurrent.Future

/***
  * Define basic write operation for communicating with InfluxDB
  * @tparam E - Entity type
  */
private[fsanaulla] trait WriteOperations[E] {

  /**
    * Execute single write to InfluxDB
    * @param dbName          - For which database
    * @param entity          - Entity that should be entered
    * @param consistency     - Consistency level
    * @param precision       - Precision level
    * @param retentionPolicy - Optional retention policy name
    * @return                - Result of execution
    */
  def _write(dbName: String,
             entity: E,
             consistency: Consistency,
             precision: Precision,
             retentionPolicy: Option[String]): Future[Result]

}
