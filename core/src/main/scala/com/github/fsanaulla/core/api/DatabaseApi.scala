package com.github.fsanaulla.core.api

import java.io.File

import com.github.fsanaulla.core.io.{ReadOperations, WriteOperations}
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.core.utils.constants.Epochs.Epoch
import com.github.fsanaulla.core.utils.constants.Precisions.Precision
import com.github.fsanaulla.core.utils.constants.{Consistencys, Epochs, Precisions}
import spray.json.JsArray

import scala.concurrent.{ExecutionContext, Future}

private[fsanaulla] abstract class DatabaseApi[E](dbName: String)
                                                (implicit ex: ExecutionContext)
  extends ReadOperations
    with WriteOperations[E] {

  final def writeFromFile0(file: File,
                           chunkSize: Int = 8192,
                           consistency: Consistency = Consistencys.ONE,
                           precision: Precision = Precisions.NANOSECONDS,
                           retentionPolicy: Option[String] = None)
                          (implicit ds: Deserializer[File, E]): Future[Result] = {
    write0(dbName, ds.deserialize(file), consistency, precision, retentionPolicy)
  }

  final def writeNative0(point: String,
                         consistency: Consistency = Consistencys.ONE,
                         precision: Precision = Precisions.NANOSECONDS,
                         retentionPolicy: Option[String] = None)
                        (implicit ds: Deserializer[String, E]): Future[Result] = {
    write0(dbName, ds.deserialize(point), consistency, precision, retentionPolicy)
  }

  final def bulkWriteNative0(points: Seq[String],
                             consistency: Consistency = Consistencys.ONE,
                             precision: Precision = Precisions.NANOSECONDS,
                             retentionPolicy: Option[String] = None)
                            (implicit ds: Deserializer[Seq[String], E]): Future[Result] = {
    write0(dbName, ds.deserialize(points), consistency, precision, retentionPolicy)
  }

  final def writePoint0(point: Point,
                        consistency: Consistency = Consistencys.ONE,
                        precision: Precision = Precisions.NANOSECONDS,
                        retentionPolicy: Option[String] = None)
                       (implicit ds: Deserializer[Point, E]): Future[Result] = {
    write0(dbName, ds.deserialize(point), consistency, precision, retentionPolicy)
  }

  final def bulkWritePoints0(points: Seq[Point],
                             consistency: Consistency = Consistencys.ONE,
                             precision: Precision = Precisions.NANOSECONDS,
                             retentionPolicy: Option[String] = None)
                            (implicit ds: Deserializer[Seq[Point], E]): Future[Result] = {
    write0(dbName, ds.deserialize(points), consistency, precision, retentionPolicy)
  }

  final def read[A](query: String,
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

  final def bulkReadJs(querys: Seq[String],
                 epoch: Epoch = Epochs.NANOSECONDS,
                 pretty: Boolean = false,
                 chunked: Boolean = false): Future[QueryResult[Seq[JsArray]]] = {
    bulkReadJs0(dbName, querys, epoch, pretty, chunked)
  }
}
