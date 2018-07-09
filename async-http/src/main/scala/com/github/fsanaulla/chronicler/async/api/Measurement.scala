package com.github.fsanaulla.chronicler.async.api

import com.github.fsanaulla.chronicler.async.io.{AsyncReader, AsyncWriter}
import com.github.fsanaulla.chronicler.core.api.MeasurementIO
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
                                     measurementName: String,
                                     gzipped: Boolean)
                                    (protected implicit val ex: ExecutionContext,
                                     protected implicit val backend: SttpBackend[Future, Nothing])
    extends MeasurementIO[Future, E, String]
      with HasCredentials
      with AsyncWriter
      with AsyncReader {

  def write(entity: E,
            consistency: Consistency = Consistencies.ONE,
            precision: Precision = Precisions.NANOSECONDS,
            retentionPolicy: Option[String] = None)
           (implicit wr: InfluxWriter[E]): Future[WriteResult] =
    writeTo(
      dbName,
      toPoint(measurementName, wr.write(entity)),
      consistency,
      precision,
      retentionPolicy,
      gzipped
    )

  def bulkWrite(entitys: Seq[E],
                consistency: Consistency = Consistencies.ONE,
                precision: Precision = Precisions.NANOSECONDS,
                retentionPolicy: Option[String] = None)
               (implicit wr: InfluxWriter[E]): Future[WriteResult] =
    writeTo(
      dbName,
      toPoints(measurementName, entitys.map(wr.write)),
      consistency,
      precision,
      retentionPolicy,
      gzipped
    )

  def read(query: String,
           epoch: Epoch = Epochs.NANOSECONDS,
           pretty: Boolean = false,
           chunked: Boolean = false)
          (implicit rd: InfluxReader[E]): Future[ReadResult[E]] = {
    readJs(dbName, query, epoch, pretty, chunked) map {
      case qr: QueryResult[JArray] => qr.map(rd.read)
      case gr: GroupedResult[JArray] => gr.map(rd.read)
    }
  }
}
