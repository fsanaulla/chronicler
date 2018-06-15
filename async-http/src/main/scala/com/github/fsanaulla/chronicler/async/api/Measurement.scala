package com.github.fsanaulla.chronicler.async.api

import com.github.fsanaulla.chronicler.async.io.{AsyncReader, AsyncWriter}
import com.github.fsanaulla.chronicler.async.models.AsyncDeserializers._
import com.github.fsanaulla.chronicler.core.api.MeasurementApi
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import com.softwaremill.sttp.SttpBackend
import jawn.ast.JArray

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

final class Measurement[E: ClassTag](val host: String,
                                     val port: Int,
                                     val credentials: Option[InfluxCredentials],
                                     dbName: String,
                                     measurementName: String)
                                    (protected implicit val ex: ExecutionContext,
                                     protected implicit val backend: SttpBackend[Future, Nothing])
    extends MeasurementApi[Future, E, String](dbName, measurementName)
    with HasCredentials
    with AsyncWriter
    with AsyncReader {

  def write(entity: E,
            consistency: Consistency = Consistencies.ONE,
            precision: Precision = Precisions.NANOSECONDS,
            retentionPolicy: Option[String] = None)
           (implicit writer: InfluxWriter[E]): Future[WriteResult] =
    write0(entity, consistency, precision, retentionPolicy)

  def bulkWrite(entitys: Seq[E],
                consistency: Consistency = Consistencies.ONE,
                precision: Precision = Precisions.NANOSECONDS,
                retentionPolicy: Option[String] = None)
               (implicit writer: InfluxWriter[E]): Future[WriteResult] =
    bulkWrite0(entitys, consistency, precision, retentionPolicy)

  def read(query: String,
           epoch: Epoch = Epochs.NANOSECONDS,
           pretty: Boolean = false,
           chunked: Boolean = false)
          (implicit rd: InfluxReader[E]): Future[ReadResult[E]] = {
    readJs0(dbName, query, epoch, pretty, chunked) map {
      case qr: QueryResult[JArray] => qr.map(rd.read)
      case gr: GroupedResult[JArray] => gr.map(rd.read)
    }
  }
}
