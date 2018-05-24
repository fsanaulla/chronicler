package com.github.fsanaulla.core.api

import com.github.fsanaulla.core.enums.{Consistency, Precision}
import com.github.fsanaulla.core.io.{ReadOperations, WriteOperations}
import com.github.fsanaulla.core.model.{Deserializer, InfluxWriter, Result}
import com.github.fsanaulla.core.utils.PointTransformer

import scala.concurrent.Future

/**
  * Main functionality for measurement api
  * @param dbName           - measurement database
  * @param measurementName  - measurement name
  * @tparam E - Entity type -
  * @tparam R
  */
private[fsanaulla] abstract class MeasurementApi[M[_], E, R](dbName: String,
                                                       measurementName: String)
  extends WriteOperations[M, R] with ReadOperations[M] with PointTransformer {

  final def write(
                   entity: E,
                   consistency: Consistency,
                   precision: Precision,
                   retentionPolicy: Option[String] = None)
                 (implicit writer: InfluxWriter[E], ds: Deserializer[String, R]): M[Result] = {

    write0(
      dbName,
      ds.deserialize(toPoint(measurementName, writer.write(entity))),
      consistency,
      precision,
      retentionPolicy
    )
  }

  final def bulkWrite(
                       entitys: Seq[E],
                       consistency: Consistency,
                       precision: Precision,
                       retentionPolicy: Option[String] = None)
                      (implicit writer: InfluxWriter[E], ds: Deserializer[String, R]): M[Result] = {

    write0(
      dbName,
      ds.deserialize(toPoints(measurementName, entitys.map(writer.write))),
      consistency,
      precision,
      retentionPolicy
    )
  }
}
