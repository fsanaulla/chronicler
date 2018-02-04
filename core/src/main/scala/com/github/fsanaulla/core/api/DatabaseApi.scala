package com.github.fsanaulla.core.api

import com.github.fsanaulla.core.io.{ReadOperations, WriteOperations}
import com.github.fsanaulla.core.model.{InfluxReader, Point, QueryResult, Result}
import com.github.fsanaulla.core.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.core.utils.constants.Epochs.Epoch
import com.github.fsanaulla.core.utils.constants.Precisions.Precision
import com.github.fsanaulla.core.utils.constants.{Consistencys, Epochs, Precisions}
import spray.json.JsArray

import scala.concurrent.Future

trait DatabaseApi[E] extends ReadOperations with WriteOperations[E] {

  def writeNative(point: String,
                  consistency: Consistency = Consistencys.ONE,
                  precision: Precision = Precisions.NANOSECONDS,
                  retentionPolicy: Option[String] = None): Future[Result]

  def bulkWriteNative(points: Seq[String],
                      consistency: Consistency = Consistencys.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[Result]

  def writeFromFile(path: String,
                    chunkSize: Int = 8192,
                    consistency: Consistency = Consistencys.ONE,
                    precision: Precision = Precisions.NANOSECONDS,
                    retentionPolicy: Option[String] = None): Future[Result]

  def writePoint(point: Point,
                 consistency: Consistency = Consistencys.ONE,
                 precision: Precision = Precisions.NANOSECONDS,
                 retentionPolicy: Option[String] = None): Future[Result]

  def bulkWritePoints(points: Seq[Point],
                      consistency: Consistency = Consistencys.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[Result]

  def read[A](query: String,
              epoch: Epoch = Epochs.NANOSECONDS,
              pretty: Boolean = false,
              chunked: Boolean = false)
             (implicit reader: InfluxReader[A]): Future[QueryResult[A]]

  def bulkReadJs(querys: Seq[String],
                 epoch: Epoch = Epochs.NANOSECONDS,
                 pretty: Boolean = false,
                 chunked: Boolean = false): Future[QueryResult[Seq[JsArray]]]
}
