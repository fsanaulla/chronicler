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

package com.github.fsanaulla.chronicler.akka.io.api

import com.github.fsanaulla.chronicler.akka.io.models.{AkkaReader, AkkaWriter}
import com.github.fsanaulla.chronicler.core.api.MeasurementApi
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 03.09.17
  */
final class Measurement[E: ClassTag](dbName: String,
                                     measurementName: String,
                                     gzipped: Boolean)
                                    (implicit ex: ExecutionContext, wr: AkkaWriter, rd: AkkaReader)
    extends MeasurementApi[Future, E] with PointTransformer {

  override def write(entity: E,
                     consistency: Option[Consistency] = None,
                     precision: Option[Precision] = None,
                     retentionPolicy: Option[String] = None)
                    (implicit writer: InfluxWriter[E]): Future[WriteResult] =
    wr.writeTo(
      dbName,
      toPoint(measurementName, writer.write(entity)),
      consistency,
      precision,
      retentionPolicy,
      gzipped
    )


  override def bulkWrite(entities: Seq[E],
                         consistency: Option[Consistency] = None,
                         precision: Option[Precision] = None,
                         retentionPolicy: Option[String] = None)
                        (implicit writer: InfluxWriter[E]): Future[WriteResult] =
    wr.writeTo(
      dbName,
      toPoints(measurementName, entities.map(writer.write)),
      consistency,
      precision,
      retentionPolicy,
      gzipped
    )


  def read(query: String,
           epoch: Option[Epoch] = None,
           pretty: Boolean = false,
           chunked: Boolean = false)(implicit reader: InfluxReader[E]): Future[ReadResult[E]] =
    rd.readJs(dbName, query, epoch, pretty, chunked).map(_.map(reader.read))

}
