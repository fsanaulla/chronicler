package com.github.fsanaulla.chronicler.async.api

import java.io.File

import com.github.fsanaulla.chronicler.async.io.{AsyncReader, AsyncWriter}
import com.github.fsanaulla.core.api.DatabaseApi
import com.github.fsanaulla.core.enums._
import com.github.fsanaulla.core.model._
import com.softwaremill.sttp.SttpBackend

import scala.concurrent.{ExecutionContext, Future}

class Database(val host: String,
               val port: Int,
               val credentials: Option[InfluxCredentials],
               dbName: String)
              (protected implicit val backend: SttpBackend[Future, Nothing],
               protected implicit val ex: ExecutionContext)
  extends DatabaseApi[String](dbName)
    with HasCredentials
    with Executable
    with AsyncWriter
    with AsyncReader {

  import com.github.fsanaulla.chronicler.async.models.AsyncDeserializers._

  def writeFromFile(file: File,
                    chunkSize: Int = 8192,
                    consistency: Consistency = Consistencies.ONE,
                    precision: Precision = Precisions.NANOSECONDS,
                    retentionPolicy: Option[String] = None): Future[Result] = {
    writeFromFile0(file, chunkSize, consistency, precision, retentionPolicy)
  }

  def writeNative(point: String,
                  consistency: Consistency = Consistencies.ONE,
                  precision: Precision = Precisions.NANOSECONDS,
                  retentionPolicy: Option[String] = None): Future[Result] = {
    writeNative0(point, consistency, precision, retentionPolicy)
  }

  def bulkWriteNative(points: Seq[String],
                      consistency: Consistency = Consistencies.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[Result] = {
    bulkWriteNative0(points, consistency, precision, retentionPolicy)
  }

  def writePoint(point: Point,
                 consistency: Consistency = Consistencies.ONE,
                 precision: Precision = Precisions.NANOSECONDS,
                 retentionPolicy: Option[String] = None): Future[Result] = {
    writePoint0(point, consistency, precision, retentionPolicy)
  }

  def bulkWritePoints(points: Seq[Point],
                      consistency: Consistency = Consistencies.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[Result] = {
    bulkWritePoints0(points, consistency, precision, retentionPolicy)
  }
}
