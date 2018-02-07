package com.github.fsanaulla.api

import java.io.File

import com.github.fsanaulla.core.api.DatabaseApi
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.core.utils.constants.Precisions.Precision
import com.github.fsanaulla.core.utils.constants.{Consistencys, Precisions}
import com.github.fsanaulla.io.{AsyncReader, AsyncWriter}
import com.softwaremill.sttp.SttpBackend

import scala.concurrent.{ExecutionContext, Future}

private[fsanaulla] class Database(dbName: String)
                                 (protected implicit val credentials: InfluxCredentials,
                                  protected implicit val backend: SttpBackend[Future, Nothing],
                                  protected implicit val ex: ExecutionContext)
  extends DatabaseApi[String](dbName)
    with AsyncWriter
    with AsyncReader {

  import com.github.fsanaulla.models.StringDeserializers._

  def writeFromFile(file: File,
                    chunkSize: Int = 8192,
                    consistency: Consistency = Consistencys.ONE,
                    precision: Precision = Precisions.NANOSECONDS,
                    retentionPolicy: Option[String] = None): Future[Result] = {
    writeFromFile0(file, chunkSize, consistency, precision, retentionPolicy)
  }

  def writeNative(point: String,
                  consistency: Consistency = Consistencys.ONE,
                  precision: Precision = Precisions.NANOSECONDS,
                  retentionPolicy: Option[String] = None): Future[Result] = {
    writeNative0(point, consistency, precision, retentionPolicy)
  }

  def bulkWriteNative(points: Seq[String],
                      consistency: Consistency = Consistencys.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[Result] = {
    bulkWriteNative0(points, consistency, precision, retentionPolicy)
  }

  def writePoint(point: Point,
                 consistency: Consistency = Consistencys.ONE,
                 precision: Precision = Precisions.NANOSECONDS,
                 retentionPolicy: Option[String] = None): Future[Result] = {
    writePoint0(point, consistency, precision, retentionPolicy)
  }

  def bulkWritePoints(points: Seq[Point],
                      consistency: Consistency = Consistencys.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[Result] = {
    bulkWritePoints0(points, consistency, precision, retentionPolicy)
  }
}