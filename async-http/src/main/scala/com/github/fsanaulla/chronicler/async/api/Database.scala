package com.github.fsanaulla.chronicler.async.api

import com.github.fsanaulla.chronicler.async.io.{AsyncReader, AsyncWriter}
import com.github.fsanaulla.chronicler.async.models.AsyncSerializers._
import com.github.fsanaulla.chronicler.core.api.DatabaseIO
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import com.softwaremill.sttp.SttpBackend
import jawn.ast.JArray

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

final class Database(val host: String,
                     val port: Int,
                     val credentials: Option[InfluxCredentials],
                     dbName: String,
                     gzipped: Boolean)(protected implicit val backend: SttpBackend[Future, Nothing],
                                       protected implicit val ex: ExecutionContext)
    extends DatabaseIO[Future, String](dbName)
      with HasCredentials
      with Executable
      with Serializable[String]
      with AsyncWriter
      with AsyncReader {

  def writeFromFile(filePath: String,
                    consistency: Consistency = Consistencies.ONE,
                    precision: Precision = Precisions.NANOSECONDS,
                    retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeFromFile(dbName, filePath, consistency, precision, retentionPolicy, gzipped)


  def writeNative(point: String,
                  consistency: Consistency = Consistencies.ONE,
                  precision: Precision = Precisions.NANOSECONDS,
                  retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeTo(dbName, point, consistency, precision, retentionPolicy, gzipped)


  def bulkWriteNative(points: Seq[String],
                      consistency: Consistency = Consistencies.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeTo(dbName, points, consistency, precision, retentionPolicy, gzipped)


  def writePoint(point: Point,
                 consistency: Consistency = Consistencies.ONE,
                 precision: Precision = Precisions.NANOSECONDS,
                 retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeTo(dbName, point, consistency, precision, retentionPolicy, gzipped)


  def bulkWritePoints(points: Seq[Point],
                      consistency: Consistency = Consistencies.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeTo(dbName, points, consistency, precision, retentionPolicy, gzipped)


  override def read[A: ClassTag](query: String,
                                 epoch: Epoch,
                                 pretty: Boolean,
                                 chunked: Boolean)
                                (implicit reader: InfluxReader[A]): Future[ReadResult[A]] = {
    readJs(query, epoch, pretty, chunked) map {
      case qr: QueryResult[JArray] => qr.map(reader.read)
      case gr: GroupedResult[JArray] => gr.map(reader.read)
    }
  }
}
