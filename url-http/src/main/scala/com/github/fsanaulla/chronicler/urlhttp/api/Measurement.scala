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

package com.github.fsanaulla.chronicler.urlhttp.api

import com.github.fsanaulla.chronicler.core.api.MeasurementIO
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.urlhttp.io.{UrlReader, UrlWriter}
import com.softwaremill.sttp.SttpBackend
import jawn.ast.JArray

import scala.reflect.ClassTag
import scala.util.Try

final class Measurement[E: ClassTag](private[urlhttp] val host: String,
                                     private[urlhttp] val port: Int,
                                     private[chronicler] val credentials: Option[InfluxCredentials],
                                     dbName: String,
                                     measurementName: String,
                                     gzipped: Boolean)
                                    (private[urlhttp] implicit val backend: SttpBackend[Try, Nothing])
  extends MeasurementIO[Try, E]
    with HasCredentials
    with UrlWriter
    with UrlReader {

  def write(entity: E,
            consistency: Consistency = Consistencies.ONE,
            precision: Precision = Precisions.NANOSECONDS,
            retentionPolicy: Option[String] = None)(implicit wr: InfluxWriter[E]): Try[WriteResult] =
    writeTo(
      dbName,
      toPoint(measurementName, wr.write(entity)),
      consistency,
      precision,
      retentionPolicy,
      gzipped
    )


  def bulkWrite(entitys: Seq[E],
                consistency: Consistency = Consistencies.ONE,
                precision: Precision = Precisions.NANOSECONDS,
                retentionPolicy: Option[String] = None)(implicit wr: InfluxWriter[E]): Try[WriteResult] =
    writeTo(
      dbName,
      toPoints(measurementName, entitys.map(wr.write)),
      consistency,
      precision,
      retentionPolicy,
      gzipped
    )


  def read(query: String,
           epoch: Epoch = Epochs.NANOSECONDS,
           pretty: Boolean = false,
           chunked: Boolean = false)(implicit rd: InfluxReader[E]): Try[ReadResult[E]] = {
    readJs(dbName, query, epoch, pretty, chunked) map {
      case qr: QueryResult[JArray] => qr.map(rd.read)
      case gr: GroupedResult[JArray] => gr.map(rd.read)
    }
  }
}