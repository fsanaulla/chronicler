package com.github.fsanaulla.chronicler.urlhttp.api

import com.github.fsanaulla.chronicler.urlhttp.io.{UrlReader, UrlWriter}
import com.github.fsanaulla.core.api.MeasurementApi
import com.github.fsanaulla.core.enums._
import com.github.fsanaulla.core.model._
import com.softwaremill.sttp.SttpBackend

import scala.reflect.ClassTag
import scala.util.Try

class Measurement[E: ClassTag](val host: String,
                               val port: Int,
                               val credentials: Option[InfluxCredentials],
                               dbName: String,
                               measurementName: String)
                              (protected implicit val backend: SttpBackend[Try, Nothing])
  extends MeasurementApi[Try, E, String](dbName, measurementName)
    with HasCredentials
    with UrlWriter
    with UrlReader {

  import com.github.fsanaulla.chronicler.urlhttp.models.UrlDeserializers._

  def write(
             entity: E,
             consistency: Consistency = Consistencies.ONE,
             precision: Precision = Precisions.NANOSECONDS,
             retentionPolicy: Option[String] = None)
           (implicit writer: InfluxWriter[E]): Try[Result] =
    write0(entity, consistency, precision, retentionPolicy)


  def bulkWrite(
                 entitys: Seq[E],
                 consistency: Consistency = Consistencies.ONE,
                 precision: Precision = Precisions.NANOSECONDS,
                 retentionPolicy: Option[String] = None)
               (implicit writer: InfluxWriter[E]): Try[Result] =
    bulkWrite0(entitys, consistency, precision, retentionPolicy)


  def read(
            query: String,
            epoch: Epoch = Epochs.NANOSECONDS,
            pretty: Boolean = false,
            chunked: Boolean = false)
          (implicit rd: InfluxReader[E]): Try[QueryResult[E]] = {
    readJs0(dbName, query, epoch, pretty, chunked).map(_.map(rd.read))
  }
}