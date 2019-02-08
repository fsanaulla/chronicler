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

package com.github.fsanaulla.chronicler.urlhttp.io.api

import com.github.fsanaulla.chronicler.core.api.DatabaseApi
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.urlhttp.io.models.{UrlReader, UrlWriter}
import com.github.fsanaulla.chronicler.urlhttp.io.serializers._
import jawn.ast.JArray

import scala.reflect.ClassTag
import scala.util.Try

final class Database(dbName: String, gzipped: Boolean)
                    (implicit wr: UrlWriter, rd: UrlReader)
  extends DatabaseApi[Try, String] with Serializable[String] {

  def writeFromFile(filePath: String,
                    consistency: Option[Consistency] = None,
                    precision: Option[Precision] = None,
                    retentionPolicy: Option[String] = None): Try[WriteResult] =
    wr.writeFromFile(dbName, filePath, consistency, precision, retentionPolicy, gzipped)

  def writeNative(point: String,
                  consistency: Option[Consistency] = None,
                  precision: Option[Precision] = None,
                  retentionPolicy: Option[String] = None): Try[WriteResult] =
    wr.writeTo(dbName, point, consistency, precision, retentionPolicy, gzipped)

  def bulkWriteNative(points: Seq[String],
                      consistency: Option[Consistency] = None,
                      precision: Option[Precision] = None,
                      retentionPolicy: Option[String] = None): Try[WriteResult] =
    wr.writeTo(dbName, points, consistency, precision, retentionPolicy, gzipped)

  def writePoint(point: Point,
                 consistency: Option[Consistency] = None,
                 precision: Option[Precision] = None,
                 retentionPolicy: Option[String] = None): Try[WriteResult] =
    wr.writeTo(dbName, point, consistency, precision, retentionPolicy, gzipped)

  def bulkWritePoints(points: Seq[Point],
                      consistency: Option[Consistency] = None,
                      precision: Option[Precision] = None,
                      retentionPolicy: Option[String] = None): Try[WriteResult] =
    wr.writeTo(dbName, points, consistency, precision, retentionPolicy, gzipped)

  override def readJs(query: String,
                      epoch: Option[Epoch] = None,
                      pretty: Boolean = false,
                      chunked: Boolean = false): Try[ReadResult[JArray]] =
    rd.readJs(dbName, query, epoch, pretty, chunked)

  override def bulkReadJs(queries: Seq[String],
                          epoch: Option[Epoch] = None,
                          pretty: Boolean = false,
                          chunked: Boolean = false): Try[QueryResult[Array[JArray]]] =
    rd.bulkReadJs(dbName, queries, epoch, pretty, chunked)


  override def read[A: ClassTag](query: String,
                                 epoch: Option[Epoch] = None,
                                 pretty: Boolean = false,
                                 chunked: Boolean = false)
                                (implicit reader: InfluxReader[A]): Try[ReadResult[A]] = {
    readJs(query, epoch, pretty, chunked) map {
      case qr: QueryResult[JArray] => qr.map(reader.read)
      case gr: GroupedResult[JArray] => gr.map(reader.read)
    }
  }
}
