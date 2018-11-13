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

package com.github.fsanaulla.chronicler.akka.io.api

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.model.RequestEntity
import _root_.akka.stream.ActorMaterializer
import com.github.fsanaulla.chronicler.akka.io.models.{AkkaReader, AkkaWriter}
import com.github.fsanaulla.chronicler.akka.io.serializers._
import com.github.fsanaulla.chronicler.akka.shared.alias.Connection
import com.github.fsanaulla.chronicler.core.api.DatabaseApi
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import jawn.ast.JArray

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
final class Database(dbName: String,
                     private[chronicler] val credentials: Option[InfluxCredentials],
                     gzipped: Boolean)
                    (private[chronicler] implicit val ex: ExecutionContext,
                     private[akka] implicit val actorSystem: ActorSystem,
                     private[akka] implicit val mat: ActorMaterializer,
                     private[akka] implicit val connection: Connection)
  extends DatabaseApi[Future, RequestEntity](dbName)
    with AkkaWriter
    with AkkaReader
    with Serializable[RequestEntity]
    with Executable
    with HasCredentials {

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

  def read[A: ClassTag](query: String,
                        epoch: Option[Epoch] = None,
                        pretty: Boolean = false,
                        chunked: Boolean = false)
                       (implicit reader: InfluxReader[A]): Future[ReadResult[A]] =
    readJs(query, epoch, pretty, chunked) map {
      case qr: QueryResult[JArray] => qr.map(reader.read)
      case gr: GroupedResult[JArray] => gr.map(reader.read)
    }
}
