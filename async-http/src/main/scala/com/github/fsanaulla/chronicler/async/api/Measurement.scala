package com.github.fsanaulla.chronicler.async.api

import com.github.fsanaulla.chronicler.async.io.AsyncWriter
import com.github.fsanaulla.core.api.MeasurementApi
import com.github.fsanaulla.core.enums.{Consistencies, Consistency, Precision, Precisions}
import com.github.fsanaulla.core.model.{HasCredentials, InfluxCredentials, InfluxWriter, Result}
import com.softwaremill.sttp.SttpBackend

import scala.concurrent.{ExecutionContext, Future}

private[fsanaulla] class Measurement[E](val host: String,
                                        val port: Int,
                                        val credentials: Option[InfluxCredentials],
                                        dbName: String,
                                        measurementName: String)
                                       (protected implicit val ex: ExecutionContext,
                                        protected implicit val backend: SttpBackend[Future, Nothing])
  extends MeasurementApi[E, String](dbName, measurementName)
    with HasCredentials
    with AsyncWriter {

  import com.github.fsanaulla.chronicler.async.models.AsyncDeserializers._

  def write(entity: E,
            consistency: Consistency = Consistencies.ONE,
            precision: Precision = Precisions.NANOSECONDS,
            retentionPolicy: Option[String] = None)
           (implicit writer: InfluxWriter[E]): Future[Result] = {
    _write0(entity, consistency, precision, retentionPolicy)
  }

  def bulkWrite(entitys: Seq[E],
                consistency: Consistency = Consistencies.ONE,
                precision: Precision = Precisions.NANOSECONDS,
                retentionPolicy: Option[String] = None)
               (implicit writer: InfluxWriter[E]): Future[Result] = {
    _bulkWrite0(entitys, consistency, precision, retentionPolicy)
  }
}