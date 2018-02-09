package com.github.fsanaulla.api

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.model.RequestEntity
import akka.stream.ActorMaterializer
import com.github.fsanaulla.core.api.DatabaseApi
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.core.utils.constants.Precisions.Precision
import com.github.fsanaulla.core.utils.constants.{Consistencys, Precisions}
import com.github.fsanaulla.io.{AkkaReader, AkkaWriter}
import com.github.fsanaulla.utils.AkkaTypeAlias.Connection

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
private[fsanaulla] class Database(dbName: String)
                                 (protected implicit val actorSystem: ActorSystem,
                                  override protected implicit val credentials: InfluxCredentials,
                                  override protected implicit val mat: ActorMaterializer,
                                  override protected implicit val ex: ExecutionContext,
                                  override protected implicit val connection: Connection)
  extends DatabaseApi[RequestEntity](dbName)
    with AkkaWriter
    with AkkaReader {

  import com.github.fsanaulla.models.HttpDeserializer._

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
