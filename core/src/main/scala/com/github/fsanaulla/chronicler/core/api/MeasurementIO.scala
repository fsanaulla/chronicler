package com.github.fsanaulla.chronicler.core.api

import com.github.fsanaulla.chronicler.core.enums.{Consistency, Precision}
import com.github.fsanaulla.chronicler.core.io.{ReadOperations, WriteOperations}
import com.github.fsanaulla.chronicler.core.model.{InfluxWriter, WriteResult}
import com.github.fsanaulla.chronicler.core.utils.PointTransformer

/**
  * Main functionality for measurement api
  * @tparam E - Entity type
  * @tparam R - Request entity type
  */
private[chronicler] trait MeasurementIO[M[_], E, R]
  extends WriteOperations[M, R]
    with ReadOperations[M]
    with PointTransformer {

  def write(entity: E,
            consistency: Consistency,
            precision: Precision,
            retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[E]): M[WriteResult]

  def bulkWrite(entitys: Seq[E],
                 consistency: Consistency,
                 precision: Precision,
                 retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[E]): M[WriteResult]
}
