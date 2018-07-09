package com.github.fsanaulla.chronicler.core.api

import com.github.fsanaulla.chronicler.core.enums.{Consistency, Precision}
import com.github.fsanaulla.chronicler.core.io.{ReadOperations, WriteOperations}
import com.github.fsanaulla.chronicler.core.model.{InfluxWriter, WriteResult}

/**
  * Main functionality for measurement api
  * @tparam E - Entity type
  * @tparam R - Request entity type
  */
trait MeasurementIO[M[_], E, R] extends WriteOperations[M, R] with ReadOperations[M] {

  /**
    * Make single write
    * @param entity          - entity to write
    * @param consistency     - consistence level
    * @param precision       - time precision
    * @param retentionPolicy - retention policy type
    * @param writer          - implicit serializer to InfluxLine format
    * @return                - Write result on backend container
    */
  def write(entity: E,
            consistency: Consistency,
            precision: Precision,
            retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[E]): M[WriteResult]

  /**
    * Make bulk write
    * @param entities        - entities to write
    * @param consistency     - consistence level
    * @param precision       - time precision
    * @param retentionPolicy - retention policy type
    * @param writer          - implicit serializer to InfluxLine format
    * @return                - Write result on backend container
    */
  def bulkWrite(entities: Seq[E],
                consistency: Consistency,
                precision: Precision,
                retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[E]): M[WriteResult]
}
