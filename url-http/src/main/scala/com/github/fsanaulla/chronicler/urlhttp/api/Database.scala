package com.github.fsanaulla.chronicler.urlhttp.api

import java.io.File

import com.github.fsanaulla.chronicler.urlhttp.io.{UrlReader, UrlWriter}
import com.github.fsanaulla.core.api.DatabaseApi
import com.github.fsanaulla.core.enums._
import com.github.fsanaulla.core.model._
import com.softwaremill.sttp.SttpBackend

import scala.reflect.ClassTag
import scala.util.Try

class Database(
                val host: String,
                val port: Int,
                val credentials: Option[InfluxCredentials],
                dbName: String)
              (protected implicit val backend: SttpBackend[Try, Nothing])
  extends DatabaseApi[Try, String](dbName)
    with HasCredentials
    with UrlWriter
    with UrlReader {

  import com.github.fsanaulla.chronicler.urlhttp.models.UrlDeserializers._

  def writeFromFile(file: File,
                    chunkSize: Int = 8192,
                    consistency: Consistency = Consistencies.ONE,
                    precision: Precision = Precisions.NANOSECONDS,
                    retentionPolicy: Option[String] = None): Try[Result] = {
    writeFromFile0(file, chunkSize, consistency, precision, retentionPolicy)
  }

  def writeNative(point: String,
                  consistency: Consistency = Consistencies.ONE,
                  precision: Precision = Precisions.NANOSECONDS,
                  retentionPolicy: Option[String] = None): Try[Result] = {
    writeNative0(point, consistency, precision, retentionPolicy)
  }

  def bulkWriteNative(points: Seq[String],
                      consistency: Consistency = Consistencies.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Try[Result] = {
    bulkWriteNative0(points, consistency, precision, retentionPolicy)
  }

  def writePoint(point: Point,
                 consistency: Consistency = Consistencies.ONE,
                 precision: Precision = Precisions.NANOSECONDS,
                 retentionPolicy: Option[String] = None): Try[Result] = {
    writePoint0(point, consistency, precision, retentionPolicy)
  }

  def bulkWritePoints(points: Seq[Point],
                      consistency: Consistency = Consistencies.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Try[Result] = {
    bulkWritePoints0(points, consistency, precision, retentionPolicy)
  }

  override def read[A: ClassTag](
                                  query: String,
                                  epoch: Epoch,
                                  pretty: Boolean,
                                  chunked: Boolean)
                                (implicit reader: InfluxReader[A]): Try[QueryResult[A]] = {
    readJs(query, epoch, pretty, chunked).map(_.map(reader.read))
  }
}
