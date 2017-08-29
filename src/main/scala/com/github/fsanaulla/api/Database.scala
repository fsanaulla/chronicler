package com.github.fsanaulla.api

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.{HttpEntity, RequestEntity}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.FileIO
import akka.util.ByteString
import com.github.fsanaulla.model._
import com.github.fsanaulla.query.DatabaseOperationQuery
import com.github.fsanaulla.utils.ContentTypes.octetStream
import com.github.fsanaulla.utils.ResponseHandler.{toBulkQueryJsResult, toQueryJsResult, toQueryResult, toResult}
import com.github.fsanaulla.utils.TypeAlias._
import com.github.fsanaulla.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.utils.constants.Epochs.Epoch
import com.github.fsanaulla.utils.constants.Precisions.Precision
import com.github.fsanaulla.utils.constants.{Consistencys, Epochs, Precisions}
import com.github.fsanaulla.utils.{PointTransformer, RequestBuilder}
import spray.json.JsArray

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by fayaz on 04.07.17.
  */
class Database(dbName: String)
              (protected implicit val credentials: InfluxCredentials,
               protected implicit val actorSystem: ActorSystem,
               protected implicit val mat: ActorMaterializer,
               protected implicit val ex: ExecutionContext,
               protected implicit val connection: Connection) extends DatabaseOperationQuery with RequestBuilder with PointTransformer {

  def write[T](measurement: String,
               entity: T,
               consistency: Consistency = Consistencys.ONE,
               precision: Precision = Precisions.NANOSECONDS,
               retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[T]): Future[Result] = {
    write(
      HttpEntity(octetStream, ByteString(toPoint(measurement, writer.write(entity)))),
      consistency,
      precision,
      retentionPolicy
    )
  }

  def bulkWrite[T](measurement: String,
                   entitys: Seq[T],
                   consistency: Consistency = Consistencys.ONE,
                   precision: Precision = Precisions.NANOSECONDS,
                   retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[T]): Future[Result] = {
    write(
      HttpEntity(octetStream, ByteString(toPoints(measurement, entitys.map(writer.write)))),
      consistency,
      precision,
      retentionPolicy
    )
  }

  def writeNative(point: String,
                  consistency: Consistency = Consistencys.ONE,
                  precision: Precision = Precisions.NANOSECONDS,
                  retentionPolicy: Option[String] = None): Future[Result] = {
    write(
      HttpEntity(ByteString(point)),
      consistency,
      precision,
      retentionPolicy
    )
  }

  def bulkWriteNative(points: Seq[String],
                      consistency: Consistency = Consistencys.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[Result] = {
    write(
      HttpEntity(ByteString(points.mkString("\n"))),
      consistency,
      precision,
      retentionPolicy
    )
  }

  def writeFromFile(path: String,
                    chunkSize: Int = 8192,
                    consistency: Consistency = Consistencys.ONE,
                    precision: Precision = Precisions.NANOSECONDS,
                    retentionPolicy: Option[String] = None): Future[Result] = {
    write(
      HttpEntity(octetStream, FileIO.fromPath(Paths.get(path), chunkSize = chunkSize)),
      consistency,
      precision,
      retentionPolicy
    )
  }

  def writePoint(point: Point,
                 consistency: Consistency = Consistencys.ONE,
                 precision: Precision = Precisions.NANOSECONDS,
                 retentionPolicy: Option[String] = None): Future[Result] = {
    write(
      HttpEntity(octetStream, ByteString(point.serialize)),
      consistency,
      precision,
      retentionPolicy
    )
  }

  def bulkWritePoints(points: Seq[Point],
                      consistency: Consistency = Consistencys.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[Result] = {
    write(
      HttpEntity(octetStream, ByteString(points.map(_.serialize).mkString("\n"))),
      consistency,
      precision,
      retentionPolicy
    )
  }

  def read[T](query: String,
              epoch: Epoch = Epochs.NANOSECONDS,
              pretty: Boolean = false,
              chunked: Boolean = false)(implicit reader: InfluxReader[T]): Future[QueryResult[T]] = {
    buildRequest(readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked), GET).flatMap(toQueryResult[T])
  }

  def readJs(query: String,
             epoch: Epoch = Epochs.NANOSECONDS,
             pretty: Boolean = false,
             chunked: Boolean = false): Future[QueryResult[JsArray]] = {
    buildRequest(readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked), GET).flatMap(toQueryJsResult)
  }

  def bulkReadJs(querys: Seq[String],
                 epoch: Epoch = Epochs.NANOSECONDS,
                 pretty: Boolean = false,
                 chunked: Boolean = false): Future[QueryResult[Seq[JsArray]]] = {
    buildRequest(readFromInfluxBulkQuery(dbName, querys, epoch, pretty, chunked), GET).flatMap(toBulkQueryJsResult)
  }

  private def write(entity: RequestEntity,
                    consistency: Consistency,
                    precision: Precision,
                    retentionPolicy: Option[String]): Future[Result] = {
    buildRequest(
      uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy),
      entity = entity
    ).flatMap(toResult)
  }
}