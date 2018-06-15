package com.github.fsanaulla.chronicler.core.api

import com.github.fsanaulla.chronicler.core.enums.{Consistency, Precision}
import com.github.fsanaulla.chronicler.core.io.{ReadOperations, WriteOperations}
import com.github.fsanaulla.chronicler.core.model.{Deserializer, InfluxWriter, WriteResult}
import com.github.fsanaulla.chronicler.core.utils.PointTransformer

/**
  * Main functionality for measurement api
  * @param dbName           - measurement database
  * @param measurementName  - measurement name
  * @tparam E - Entity type -
  * @tparam R
  */
private[chronicler] abstract class MeasurementApi[M[_], E, R](dbName: String, measurementName: String)
  extends WriteOperations[M, R] with ReadOperations[M] with PointTransformer {

  final def write0(
                   entity: E,
                   consistency: Consistency,
                   precision: Precision,
                   retentionPolicy: Option[String] = None)
                 (implicit writer: InfluxWriter[E], ds: Deserializer[String, R]): M[WriteResult] = {

    writeTo(
      dbName,
      ds.deserialize(toPoint(measurementName, writer.write(entity))),
      consistency,
      precision,
      retentionPolicy
    )
  }

  final def bulkWrite0(
                       entitys: Seq[E],
                       consistency: Consistency,
                       precision: Precision,
                       retentionPolicy: Option[String] = None)
                      (implicit writer: InfluxWriter[E], ds: Deserializer[String, R]): M[WriteResult] = {

    writeTo(
      dbName,
      ds.deserialize(toPoints(measurementName, entitys.map(writer.write))),
      consistency,
      precision,
      retentionPolicy
    )
  }
}
