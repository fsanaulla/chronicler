package com.github.fsanaulla.db

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpMethods.GET
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.FileIO
import akka.util.ByteString
import com.github.fsanaulla.model._
import com.github.fsanaulla.utils.ContentTypes.octetStream
import com.github.fsanaulla.utils.ResponseHandler.{toBulkQueryJsResult, toQueryJsResult, toQueryResult}
import com.github.fsanaulla.utils.TypeAlias._
import com.github.fsanaulla.utils.WriteHelpersOperation
import com.github.fsanaulla.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.utils.constants.Epochs.Epoch
import com.github.fsanaulla.utils.constants.Precisions.Precision
import com.github.fsanaulla.utils.constants.{Consistencys, Epochs, Precisions}
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
               protected implicit val connection: Connection) extends WriteHelpersOperation {

  def measurement[A](measurementName: String): Measurement[A] = {
    new Measurement[A](dbName, measurementName)
  }


  def writeNative(point: String,
                  consistency: Consistency = Consistencys.ONE,
                  precision: Precision = Precisions.NANOSECONDS,
                  retentionPolicy: Option[String] = None): Future[Result] = {
    write(
      dbName,
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
      dbName,
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

    val entity = HttpEntity(
      octetStream,
      FileIO.fromPath(Paths.get(path), chunkSize = chunkSize)
    )

    write(dbName, entity, consistency, precision, retentionPolicy)
  }

  def writePoint(point: Point,
                 consistency: Consistency = Consistencys.ONE,
                 precision: Precision = Precisions.NANOSECONDS,
                 retentionPolicy: Option[String] = None): Future[Result] = {

    val entity = HttpEntity(
      octetStream,
      ByteString(point.serialize)
    )
    write(dbName, entity, consistency, precision, retentionPolicy)
  }

  def bulkWritePoints(points: Seq[Point],
                      consistency: Consistency = Consistencys.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[Result] = {
    write(
      dbName,
      HttpEntity(
        octetStream,
        ByteString(points.map(_.serialize).mkString("\n"))
      ),
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
}
