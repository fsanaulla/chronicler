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

package com.github.fsanaulla.chronicler.ahc.io.api

import com.github.fsanaulla.chronicler.ahc.io.models.{AhcReader, AhcWriter}
import com.github.fsanaulla.chronicler.ahc.io.serializers._
import com.github.fsanaulla.chronicler.core.api.DatabaseApi
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import com.softwaremill.sttp.SttpBackend
import jawn.ast.JArray

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

final class Database(private[ahc] val host: String,
                     private[ahc] val port: Int,
                     private[chronicler] val credentials: Option[InfluxCredentials],
                     dbName: String,
                     gzipped: Boolean)(private[ahc] implicit val backend: SttpBackend[Future, Nothing],
                                       private[chronicler] implicit val ex: ExecutionContext)
    extends DatabaseApi[Future, String](dbName)
      with HasCredentials
      with Executable
      with Serializable[String]
      with AhcWriter
      with AhcReader {

  def writeFromFile(filePath: String,
                    consistency: Option[Consistency] = None,
                    precision: Option[Precision] = None,
                    retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeFromFile(dbName, filePath, consistency, precision, retentionPolicy, gzipped)


  def writeNative(point: String,
                  consistency: Option[Consistency] = None,
                  precision: Option[Precision] = None,
                  retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeTo(dbName, point, consistency, precision, retentionPolicy, gzipped)


  def bulkWriteNative(points: Seq[String],
                      consistency: Option[Consistency] = None,
                      precision: Option[Precision] = None,
                      retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeTo(dbName, points, consistency, precision, retentionPolicy, gzipped)


  def writePoint(point: Point,
                 consistency: Option[Consistency] = None,
                 precision: Option[Precision] = None,
                 retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeTo(dbName, point, consistency, precision, retentionPolicy, gzipped)


  def bulkWritePoints(points: Seq[Point],
                      consistency: Option[Consistency] = None,
                      precision: Option[Precision] = None,
                      retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeTo(dbName, points, consistency, precision, retentionPolicy, gzipped)


  override def read[A: ClassTag](query: String,
                                 epoch: Option[Epoch] = None,
                                 pretty: Boolean = false,
                                 chunked: Boolean = false)
                                (implicit reader: InfluxReader[A]): Future[ReadResult[A]] = {
    readJs(query, epoch, pretty, chunked) map {
      case qr: QueryResult[JArray] => qr.map(reader.read)
      case gr: GroupedResult[JArray] => gr.map(reader.read)
    }
  }
}
