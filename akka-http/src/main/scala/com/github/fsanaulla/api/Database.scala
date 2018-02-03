package com.github.fsanaulla.api

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, RequestEntity}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.FileIO
import com.github.fsanaulla.core.api.DatabaseApi
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.core.utils.constants.Epochs.Epoch
import com.github.fsanaulla.core.utils.constants.Precisions.Precision
import com.github.fsanaulla.core.utils.constants.{Consistencys, Epochs, Precisions}
import com.github.fsanaulla.io.{AkkaReader, AkkaWriter}
import com.github.fsanaulla.models.HttpWriters._
import com.github.fsanaulla.utils.AkkaContentTypes.OctetStream
import com.github.fsanaulla.utils.AkkaTypeAlias.Connection
import spray.json.JsArray

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
private[fsanaulla] class Database(dbName: String)
                                 (protected implicit val credentials: InfluxCredentials,
                                  protected implicit val actorSystem: ActorSystem,
                                  protected implicit val mat: ActorMaterializer,
                                  protected implicit val ex: ExecutionContext,
                                  protected implicit val connection: Connection)
  extends DatabaseApi[RequestEntity] with AkkaWriter with AkkaReader {

  def writeNative(point: String,
                  consistency: Consistency = Consistencys.ONE,
                  precision: Precision = Precisions.NANOSECONDS,
                  retentionPolicy: Option[String] = None): Future[Result] = {

    write0(dbName, point, consistency, precision, retentionPolicy)
  }

  def bulkWriteNative(points: Seq[String],
                      consistency: Consistency = Consistencys.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[Result] = {

    write0(dbName, points, consistency, precision, retentionPolicy)
  }

  def writeFromFile(path: String,
                    chunkSize: Int = 8192,
                    consistency: Consistency = Consistencys.ONE,
                    precision: Precision = Precisions.NANOSECONDS,
                    retentionPolicy: Option[String] = None): Future[Result] = {

    val entity = HttpEntity(
      OctetStream,
      FileIO.fromPath(Paths.get(path), chunkSize = chunkSize)
    )

    write0(dbName, entity, consistency, precision, retentionPolicy)
  }

  def writePoint(point: Point,
                 consistency: Consistency = Consistencys.ONE,
                 precision: Precision = Precisions.NANOSECONDS,
                 retentionPolicy: Option[String] = None): Future[Result] = {

    write0(dbName, point, consistency, precision, retentionPolicy)
  }

  def bulkWritePoints(points: Seq[Point],
                      consistency: Consistency = Consistencys.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[Result] = {

    write0(dbName, points, consistency, precision, retentionPolicy)
  }

  def read[A](query: String,
              epoch: Epoch = Epochs.NANOSECONDS,
              pretty: Boolean = false,
              chunked: Boolean = false)
             (implicit reader: InfluxReader[A]): Future[QueryResult[A]] = {

    readJs0(dbName, query, epoch, pretty, chunked) map { res =>
      QueryResult[A](
        res.code,
        isSuccess = res.isSuccess,
        res.queryResult.map(reader.read),
        res.ex)
    }
  }

  def bulkReadJs(querys: Seq[String],
                 epoch: Epoch = Epochs.NANOSECONDS,
                 pretty: Boolean = false,
                 chunked: Boolean = false): Future[QueryResult[Seq[JsArray]]] = {
    bulkReadJs0(dbName, querys, epoch, pretty, chunked)
  }
}
