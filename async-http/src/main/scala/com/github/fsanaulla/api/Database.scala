package com.github.fsanaulla.api

import com.github.fsanaulla.core.api.DatabaseApi
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.utils.constants.{Consistencys, Epochs, Precisions}
import com.github.fsanaulla.io.{AsyncReader, AsyncWriter}
import com.softwaremill.sttp.SttpBackend
import spray.json.JsArray

import scala.concurrent.{ExecutionContext, Future}

private[fsanaulla] class Database(dbNamae: String)
                                 (override protected implicit val credentials: InfluxCredentials,
                                  override protected implicit val backend: SttpBackend[Future, Nothing],
                                  override protected implicit val ex: ExecutionContext)
  extends DatabaseApi[String]
    with AsyncWriter
    with AsyncReader {

  override def writeNative(point: String, consistency: Consistencys.Consistency, precision: Precisions.Precision, retentionPolicy: Option[String]): Future[Result] = ???

  override def bulkWriteNative(points: Seq[String], consistency: Consistencys.Consistency, precision: Precisions.Precision, retentionPolicy: Option[String]): Future[Result] = ???

  override def writeFromFile(path: String, chunkSize: Int, consistency: Consistencys.Consistency, precision: Precisions.Precision, retentionPolicy: Option[String]): Future[Result] = ???

  override def writePoint(point: Point, consistency: Consistencys.Consistency, precision: Precisions.Precision, retentionPolicy: Option[String]): Future[Result] = ???

  override def bulkWritePoints(points: Seq[Point], consistency: Consistencys.Consistency, precision: Precisions.Precision, retentionPolicy: Option[String]): Future[Result] = ???

  override def read[A](query: String, epoch: Epochs.Epoch, pretty: Boolean, chunked: Boolean)(implicit reader: InfluxReader[A]): Future[QueryResult[A]] = ???

  override def bulkReadJs(querys: Seq[String], epoch: Epochs.Epoch, pretty: Boolean, chunked: Boolean): Future[QueryResult[Seq[JsArray]]] = ???

  override def write0(dbName: String, entity: String, consistency: Consistencys.Consistency, precision: Precisions.Precision, retentionPolicy: Option[String]): Future[Result] = ???
}
