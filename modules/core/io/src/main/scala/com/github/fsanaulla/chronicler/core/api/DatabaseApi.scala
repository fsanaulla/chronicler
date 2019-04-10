/*
 * Copyright 2017-2019 Faiaz Sanaulla
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

import java.io.File

import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import jawn.ast.JArray

import scala.reflect.ClassTag

/**
  * Generic interface for basic database IO operation
  * @tparam F - container type
  * @tparam E - Entity type
  */
trait DatabaseApi[F[_], E] {

  def read[A: ClassTag](query: String,
                        epoch: Option[Epoch],
                        pretty: Boolean,
                        chunked: Boolean)(implicit reader: InfluxReader[A]): F[ReadResult[A]]

  def writeFromFile(file: File,
                    consistency: Option[Consistency],
                    precision: Option[Precision],
                    retentionPolicy: Option[String]): F[WriteResult]


  def writeNative(point: String,
                  consistency: Option[Consistency],
                  precision: Option[Precision],
                  retentionPolicy: Option[String]): F[WriteResult]


  def bulkWriteNative(points: Seq[String],
                      consistency: Option[Consistency],
                      precision: Option[Precision],
                      retentionPolicy: Option[String]): F[WriteResult]


  def writePoint(point: Point,
                 consistency: Option[Consistency],
                 precision: Option[Precision],
                 retentionPolicy: Option[String]): F[WriteResult]


  def bulkWritePoints(points: Seq[Point],
                      consistency: Option[Consistency],
                      precision: Option[Precision],
                      retentionPolicy: Option[String]): F[WriteResult]


  def readJson(query: String,
               epoch: Option[Epoch],
               pretty: Boolean,
               chunked: Boolean): F[ReadResult[JArray]]

  def bulkReadJson(queries: Seq[String],
                   epoch: Option[Epoch],
                   pretty: Boolean,
                   chunked: Boolean): F[QueryResult[Array[JArray]]]

  def readGroupedJson(query: String,
                      epoch: Option[Epoch],
                      pretty: Boolean,
                      chunked: Boolean): F[ReadResult[JArray]]
}
