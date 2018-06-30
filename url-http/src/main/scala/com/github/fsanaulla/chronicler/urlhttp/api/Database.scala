package com.github.fsanaulla.chronicler.urlhttp.api

import com.github.fsanaulla.chronicler.core.api.DatabaseIO
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.urlhttp.io.{UrlReader, UrlWriter}
import com.github.fsanaulla.chronicler.urlhttp.models.UrlDeserializers._
import com.softwaremill.sttp.SttpBackend
import jawn.ast.JArray

import scala.reflect.ClassTag
import scala.util.Try

final class Database(dbName: String,
                     gzipped: Boolean,
                     val host: String,
                     val port: Int,
                     val credentials: Option[InfluxCredentials])
                    (protected implicit val backend: SttpBackend[Try, Nothing])
  extends DatabaseIO[Try, String](dbName)
    with HasCredentials
    with Serializable[String]
    with UrlWriter
    with UrlReader {

  def writeFromFile(filePath: String,
                    consistency: Consistency = Consistencies.ONE,
                    precision: Precision = Precisions.NANOSECONDS,
                    retentionPolicy: Option[String] = None): Try[WriteResult] =
    writeFromFile(dbName, filePath, consistency, precision, retentionPolicy, gzipped)

  def writeNative(point: String,
                  consistency: Consistency = Consistencies.ONE,
                  precision: Precision = Precisions.NANOSECONDS,
                  retentionPolicy: Option[String] = None): Try[WriteResult] =
    writeTo(dbName, point, consistency, precision, retentionPolicy, gzipped)

  def bulkWriteNative(points: Seq[String],
                      consistency: Consistency = Consistencies.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Try[WriteResult] =
    writeTo(dbName, points, consistency, precision, retentionPolicy, gzipped)

  def writePoint(point: Point,
                 consistency: Consistency = Consistencies.ONE,
                 precision: Precision = Precisions.NANOSECONDS,
                 retentionPolicy: Option[String] = None): Try[WriteResult] =
    writeTo(dbName, point, consistency, precision, retentionPolicy, gzipped)

  def bulkWritePoints(points: Seq[Point],
                      consistency: Consistency = Consistencies.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Try[WriteResult] =
    writeTo(dbName, points, consistency, precision, retentionPolicy, gzipped)

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
