package com.github.fsanaulla.chronicler.async.api

import com.github.fsanaulla.chronicler.async.io.{AsyncReader, AsyncWriter}
import com.github.fsanaulla.core.api.MeasurementApi
import com.github.fsanaulla.core.enums._
import com.github.fsanaulla.core.model._
import com.softwaremill.sttp.SttpBackend

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

class Measurement[E: ClassTag](val host: String,
                               val port: Int,
                               val credentials: Option[InfluxCredentials],
                               dbName: String,
                               measurementName: String)
                              (protected implicit val ex: ExecutionContext,
                               protected implicit val backend: SttpBackend[Future, Nothing])
  extends MeasurementApi[E, String](dbName, measurementName)
    with HasCredentials
    with AsyncWriter
    with AsyncReader {

  import com.github.fsanaulla.chronicler.async.models.AsyncDeserializers._

  def write(entity: E,
            consistency: Consistency = Consistencies.ONE,
            precision: Precision = Precisions.NANOSECONDS,
            retentionPolicy: Option[String] = None)
           (implicit writer: InfluxWriter[E]): Future[Result] = {
    write(entity, consistency, precision, retentionPolicy)
  }

  def bulkWrite(entitys: Seq[E],
                consistency: Consistency = Consistencies.ONE,
                precision: Precision = Precisions.NANOSECONDS,
                retentionPolicy: Option[String] = None)
               (implicit writer: InfluxWriter[E]): Future[Result] = {
    _bulkWrite0(entitys, consistency, precision, retentionPolicy)
  }

  def read(query: String,
           epoch: Epoch = Epochs.NANOSECONDS,
           pretty: Boolean = false,
           chunked: Boolean = false)
          (implicit rd: InfluxReader[E]): Future[QueryResult[E]] = {
    _readJs(dbName, query, epoch, pretty, chunked).map(_.map(rd.read))
  }
}