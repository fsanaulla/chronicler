package com.github.fsanaulla.chronicler.core.api

import java.io.File

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
private[chronicler] abstract class DatabaseApi[M[_], E](dbName: String)
  extends ReadOperations[M] with WriteOperations[M, E] {

  def read[A: ClassTag](query: String,
                        epoch: Epoch = Epochs.NANOSECONDS,
                        pretty: Boolean = false,
                        chunked: Boolean = false)
                       (implicit reader: InfluxReader[A]): M[ReadResult[A]]

  final def writeFromFile0(file: File,
                           chunkSize: Int = 8192,
                           consistency: Consistency = Consistencies.ONE,
                           precision: Precision = Precisions.NANOSECONDS,
                           retentionPolicy: Option[String] = None)
                          (implicit ds: Deserializer[File, E]): M[WriteResult] =
    writeTo(dbName, ds.deserialize(file), consistency, precision, retentionPolicy)


  final def writeNative0(point: String,
                         consistency: Consistency = Consistencies.ONE,
                         precision: Precision = Precisions.NANOSECONDS,
                         retentionPolicy: Option[String] = None)
                        (implicit ds: Deserializer[String, E]): M[WriteResult] =
    writeTo(dbName, ds.deserialize(point), consistency, precision, retentionPolicy)


  final def bulkWriteNative0(points: Seq[String],
                             consistency: Consistency = Consistencies.ONE,
                             precision: Precision = Precisions.NANOSECONDS,
                             retentionPolicy: Option[String] = None)
                            (implicit ds: Deserializer[Seq[String], E]): M[WriteResult] =
    writeTo(dbName, ds.deserialize(points), consistency, precision, retentionPolicy)


  final def writePoint0(point: Point,
                        consistency: Consistency = Consistencies.ONE,
                        precision: Precision = Precisions.NANOSECONDS,
                        retentionPolicy: Option[String] = None)
                       (implicit ds: Deserializer[Point, E]): M[WriteResult] =
    writeTo(dbName, ds.deserialize(point), consistency, precision, retentionPolicy)


  final def bulkWritePoints0(points: Seq[Point],
                             consistency: Consistency = Consistencies.ONE,
                             precision: Precision = Precisions.NANOSECONDS,
                             retentionPolicy: Option[String] = None)
                            (implicit ds: Deserializer[Seq[Point], E]): M[WriteResult] =
    writeTo(dbName, ds.deserialize(points), consistency, precision, retentionPolicy)


  final def readJs(query: String,
                   epoch: Epoch = Epochs.NANOSECONDS,
                   pretty: Boolean = false,
                   chunked: Boolean = false): M[ReadResult[JArray]] =
    readJs0(dbName, query, epoch, pretty, chunked)


  final def bulkReadJs(queries: Seq[String],
                       epoch: Epoch = Epochs.NANOSECONDS,
                       pretty: Boolean = false,
                       chunked: Boolean = false): M[QueryResult[Array[JArray]]] =
    bulkReadJs0(dbName, queries, epoch, pretty, chunked)
}
