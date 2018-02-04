package com.github.fsanaulla.core.api

import com.github.fsanaulla.core.io.WriteOperations
import com.github.fsanaulla.core.model.{InfluxWriter, Result}
import com.github.fsanaulla.core.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.core.utils.constants.Precisions.Precision

import scala.concurrent.Future

private[fsanaulla] trait MeasurementApi[E, R] extends WriteOperations[R] {

  def write(entity: E,
            consistency: Consistency,
            precision: Precision,
            retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[E]): Future[Result]

  def bulkWrite(entitys: Seq[E],
                consistency: Consistency,
                precision: Precision,
                retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[E]): Future[Result]
}
