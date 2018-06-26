package com.github.fsanaulla.chronicler.urlhttp.api

import com.github.fsanaulla.chronicler.core.api.MeasurementIO
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.urlhttp.io.{UrlReader, UrlWriter}
import com.softwaremill.sttp.SttpBackend
import jawn.ast.JArray

import scala.reflect.ClassTag
import scala.util.Try

final class Measurement[E: ClassTag](val host: String,
                               val port: Int,
                               val credentials: Option[InfluxCredentials],
                               dbName: String,
                               measurementName: String)
                              (protected implicit val backend: SttpBackend[Try, Nothing])
  extends MeasurementIO[Try, E, String]
    with HasCredentials
    with UrlWriter
    with UrlReader {

  def write(entity: E,
            consistency: Consistency = Consistencies.ONE,
            precision: Precision = Precisions.NANOSECONDS,
            retentionPolicy: Option[String] = None)(implicit wr: InfluxWriter[E]): Try[WriteResult] =
    writeTo(dbName, toPoint(measurementName, wr.write(entity)), consistency, precision, retentionPolicy)


  def bulkWrite(entitys: Seq[E],
                consistency: Consistency = Consistencies.ONE,
                precision: Precision = Precisions.NANOSECONDS,
                retentionPolicy: Option[String] = None)(implicit wr: InfluxWriter[E]): Try[WriteResult] =
    writeTo(dbName, toPoints(measurementName, entitys.map(wr.write)), consistency, precision, retentionPolicy)


  def read(query: String,
           epoch: Epoch = Epochs.NANOSECONDS,
           pretty: Boolean = false,
           chunked: Boolean = false)(implicit rd: InfluxReader[E]): Try[ReadResult[E]] = {
    readJs(dbName, query, epoch, pretty, chunked) map {
      case qr: QueryResult[JArray] => qr.map(rd.read)
      case gr: GroupedResult[JArray] => gr.map(rd.read)
    }
  }
}