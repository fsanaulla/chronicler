/*
 * Copyright 2017-2018 Faiaz Sanaulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fsanaulla.chronicler.core.api

import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.io.ReadOperations
import com.github.fsanaulla.chronicler.core.model._
import jawn.ast.JArray

import scala.reflect.ClassTag

/**
  * Generic interface for basic database IO operation
  * @param dbName - database name
  * @tparam F     - container type
  * @tparam E     - Entity type
  */
abstract class DatabaseApi[F[_], E](dbName: String) extends ReadOperations[F] {

  def read[A: ClassTag](query: String,
                        epoch: Option[Epoch],
                        pretty: Boolean = false,
                        chunked: Boolean = false)(implicit reader: InfluxReader[A]): F[ReadResult[A]]

  def writeFromFile(filePath: String,
                    consistency: Option[Consistency],
                    precision: Option[Precision],
                    retentionPolicy: Option[String] = None): F[WriteResult]


  def writeNative(point: String,
                  consistency: Option[Consistency],
                  precision: Option[Precision],
                  retentionPolicy: Option[String] = None): F[WriteResult]


  def bulkWriteNative(points: Seq[String],
                      consistency: Option[Consistency],
                      precision: Option[Precision],
                      retentionPolicy: Option[String] = None): F[WriteResult]


  def writePoint(point: Point,
                 consistency: Option[Consistency],
                 precision: Option[Precision],
                 retentionPolicy: Option[String] = None): F[WriteResult]


  def bulkWritePoints(points: Seq[Point],
                      consistency: Option[Consistency],
                      precision: Option[Precision],
                      retentionPolicy: Option[String] = None): F[WriteResult]


  final def readJs(query: String,
                   epoch: Option[Epoch] = None,
                   pretty: Boolean = false,
                   chunked: Boolean = false): F[ReadResult[JArray]] =
    readJs(dbName, query, epoch, pretty, chunked)


  final def bulkReadJs(queries: Seq[String],
                       epoch: Option[Epoch] = None,
                       pretty: Boolean = false,
                       chunked: Boolean = false): F[QueryResult[Array[JArray]]] =
    bulkReadJs(dbName, queries, epoch, pretty, chunked)
}
