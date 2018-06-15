package com.github.fsanaulla.chronicler.urlhttp.api

import java.io.File

import com.github.fsanaulla.chronicler.core.api.DatabaseApi
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.urlhttp.io.{UrlReader, UrlWriter}
import com.github.fsanaulla.chronicler.urlhttp.models.UrlDeserializers._
import com.softwaremill.sttp.SttpBackend
import jawn.ast.JArray

import scala.reflect.ClassTag
import scala.util.Try

final class Database(
                val host: String,
                val port: Int,
                val credentials: Option[InfluxCredentials],
                dbName: String)
              (protected implicit val backend: SttpBackend[Try, Nothing])
  extends DatabaseApi[Try, String](dbName)
    with HasCredentials
    with UrlWriter
    with UrlReader {

  def writeFromFile(file: File,
                    chunkSize: Int = 8192,
                    consistency: Consistency = Consistencies.ONE,
                    precision: Precision = Precisions.NANOSECONDS,
                    retentionPolicy: Option[String] = None): Try[WriteResult] = {
    writeFromFile0(file, chunkSize, consistency, precision, retentionPolicy)
  }

  def writeNative(point: String,
                  consistency: Consistency = Consistencies.ONE,
                  precision: Precision = Precisions.NANOSECONDS,
                  retentionPolicy: Option[String] = None): Try[WriteResult] = {
    writeNative0(point, consistency, precision, retentionPolicy)
  }

  def bulkWriteNative(points: Seq[String],
                      consistency: Consistency = Consistencies.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Try[WriteResult] = {
    bulkWriteNative0(points, consistency, precision, retentionPolicy)
  }

  def writePoint(point: Point,
                 consistency: Consistency = Consistencies.ONE,
                 precision: Precision = Precisions.NANOSECONDS,
                 retentionPolicy: Option[String] = None): Try[WriteResult] = {
    writePoint0(point, consistency, precision, retentionPolicy)
  }

  def bulkWritePoints(points: Seq[Point],
                      consistency: Consistency = Consistencies.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Try[WriteResult] = {
    bulkWritePoints0(points, consistency, precision, retentionPolicy)
  }

  override def read[A: ClassTag](
                                  query: String,
                                  epoch: Epoch,
                                  pretty: Boolean,
                                  chunked: Boolean)
                                (implicit reader: InfluxReader[A]): Try[ReadResult[A]] = {
    readJs(query, epoch, pretty, chunked) map {
      case qr: QueryResult[JArray] => qr.map(reader.read)
      case gr: GroupedResult[JArray] => gr.map(reader.read)
    }
  }
}
