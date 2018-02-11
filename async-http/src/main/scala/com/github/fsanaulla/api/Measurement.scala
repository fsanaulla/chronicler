package com.github.fsanaulla.api

import com.github.fsanaulla.core.api.MeasurementApi
import com.github.fsanaulla.core.model.{InfluxCredentials, InfluxWriter, Result}
import com.github.fsanaulla.core.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.core.utils.constants.Precisions.Precision
import com.github.fsanaulla.core.utils.constants.{Consistencys, Precisions}
import com.github.fsanaulla.io.AsyncWriter
import com.softwaremill.sttp.SttpBackend

import scala.concurrent.{ExecutionContext, Future}

private[fsanaulla] class Measurement[E](val host: String, val port: Int, dbName: String, measurementName: String)
                                       (protected implicit val ex: ExecutionContext,
                                        protected implicit val credentials: InfluxCredentials,
                                        protected implicit val backend: SttpBackend[Future, Nothing])
  extends MeasurementApi[E, String](dbName, measurementName) with AsyncWriter {

  import com.github.fsanaulla.models.AsyncDeserializers._

  def write(entity: E,
            consistency: Consistency = Consistencys.ONE,
            precision: Precision = Precisions.NANOSECONDS,
            retentionPolicy: Option[String] = None)
           (implicit writer: InfluxWriter[E]): Future[Result] = {
    write0(entity, consistency, precision, retentionPolicy)
  }

  def bulkWrite(entitys: Seq[E],
                consistency: Consistency = Consistencys.ONE,
                precision: Precision = Precisions.NANOSECONDS,
                retentionPolicy: Option[String] = None)
               (implicit writer: InfluxWriter[E]): Future[Result] = {
    bulkWrite0(entitys, consistency, precision, retentionPolicy)
  }
}