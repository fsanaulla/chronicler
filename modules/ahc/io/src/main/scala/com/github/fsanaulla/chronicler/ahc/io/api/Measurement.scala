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

package com.github.fsanaulla.chronicler.ahc.io.api

import com.github.fsanaulla.chronicler.ahc.io.models.{AhcReader, AhcWriter}
import com.github.fsanaulla.chronicler.core.api.MeasurementApi
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import com.softwaremill.sttp.SttpBackend
import jawn.ast.JArray

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

final class Measurement[E: ClassTag](private[ahc] val host: String,
                                     private[ahc] val port: Int,
                                     private[chronicler] val credentials: Option[InfluxCredentials],
                                     dbName: String,
                                     measurementName: String,
                                     gzipped: Boolean)
                                    (private[chronicler] implicit val ex: ExecutionContext,
                                     private[ahc] implicit val backend: SttpBackend[Future, Nothing])
    extends MeasurementApi[Future, E]
      with HasCredentials
      with AhcWriter
      with AhcReader {

  def write(entity: E,
            consistency: Option[Consistency] = None,
            precision: Option[Precision] = None,
            retentionPolicy: Option[String] = None)
           (implicit wr: InfluxWriter[E]): Future[WriteResult] =
    writeTo(
      dbName,
      toPoint(measurementName, wr.write(entity)),
      consistency,
      precision,
      retentionPolicy,
      gzipped
    )

  def bulkWrite(entitys: Seq[E],
                consistency: Option[Consistency] = None,
                precision: Option[Precision] = None,
                retentionPolicy: Option[String] = None)
               (implicit wr: InfluxWriter[E]): Future[WriteResult] =
    writeTo(
      dbName,
      toPoints(measurementName, entitys.map(wr.write)),
      consistency,
      precision,
      retentionPolicy,
      gzipped
    )

  def read(query: String,
           epoch: Option[Epoch] = None,
           pretty: Boolean = false,
           chunked: Boolean = false)
          (implicit rd: InfluxReader[E]): Future[ReadResult[E]] = {
    readJs(dbName, query, epoch, pretty, chunked) map {
      case qr: QueryResult[JArray] => qr.map(rd.read)
      case gr: GroupedResult[JArray] => gr.map(rd.read)
    }
  }
}
