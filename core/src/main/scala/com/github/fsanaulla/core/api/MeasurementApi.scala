package com.github.fsanaulla.core.api

import com.github.fsanaulla.core.io.WriteOperations
import com.github.fsanaulla.core.model.{Deserializer, InfluxWriter, Result}
import com.github.fsanaulla.core.utils.PointTransformer
import com.github.fsanaulla.core.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.core.utils.constants.Precisions.Precision

import scala.concurrent.Future


//todo: implement typesafe read operation
private[fsanaulla] abstract class MeasurementApi[E, R](dbName: String,
                                                       measurementName: String)
  extends WriteOperations[R] with PointTransformer {

  final def write0(entity: E,
                   consistency: Consistency,
                   precision: Precision,
                   retentionPolicy: Option[String] = None)
                  (implicit writer: InfluxWriter[E], ds: Deserializer[String, R]): Future[Result] = {

    write0(
      dbName,
      ds.deserialize(toPoint(measurementName, writer.write(entity))),
      consistency,
      precision,
      retentionPolicy
    )
  }

  final def bulkWrite0(entitys: Seq[E],
                       consistency: Consistency,
                       precision: Precision,
                       retentionPolicy: Option[String] = None)
                      (implicit writer: InfluxWriter[E], ds: Deserializer[String, R]): Future[Result] = {

    write0(
      dbName,
      ds.deserialize(toPoints(measurementName, entitys.map(writer.write))),
      consistency,
      precision,
      retentionPolicy
    )
  }
}
