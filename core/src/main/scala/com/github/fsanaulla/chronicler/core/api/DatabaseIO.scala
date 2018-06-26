package com.github.fsanaulla.chronicler.core.api

import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.io.{ReadOperations, WriteOperations}
import com.github.fsanaulla.chronicler.core.model._
import jawn.ast.JArray

import scala.reflect.ClassTag

/**
  * Generic interface for basic database IO operation
  * @param dbName - database name
  * @tparam M     - container type
  * @tparam E     - Entity type
  */
private[chronicler] abstract class DatabaseIO[M[_], E](dbName: String)
  extends ReadOperations[M]
    with WriteOperations[M, E] {

  def read[A: ClassTag](query: String,
                        epoch: Epoch = Epochs.NANOSECONDS,
                        pretty: Boolean = false,
                        chunked: Boolean = false)(implicit reader: InfluxReader[A]): M[ReadResult[A]]

  def writeFromFile(filePath: String,
                    consistency: Consistency = Consistencies.ONE,
                    precision: Precision = Precisions.NANOSECONDS,
                    retentionPolicy: Option[String] = None): M[WriteResult]


  def writeNative(point: String,
                  consistency: Consistency = Consistencies.ONE,
                  precision: Precision = Precisions.NANOSECONDS,
                  retentionPolicy: Option[String] = None): M[WriteResult]


  def bulkWriteNative(points: Seq[String],
                      consistency: Consistency = Consistencies.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): M[WriteResult]


  def writePoint(point: Point,
                 consistency: Consistency = Consistencies.ONE,
                 precision: Precision = Precisions.NANOSECONDS,
                 retentionPolicy: Option[String] = None): M[WriteResult]


  def bulkWritePoints(points: Seq[Point],
                      consistency: Consistency = Consistencies.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): M[WriteResult]


  final def readJs(query: String,
                   epoch: Epoch = Epochs.NANOSECONDS,
                   pretty: Boolean = false,
                   chunked: Boolean = false): M[ReadResult[JArray]] =
    readJs(dbName, query, epoch, pretty, chunked)


  final def bulkReadJs(queries: Seq[String],
                       epoch: Epoch = Epochs.NANOSECONDS,
                       pretty: Boolean = false,
                       chunked: Boolean = false): M[QueryResult[Array[JArray]]] =
    bulkReadJs(dbName, queries, epoch, pretty, chunked)
}
