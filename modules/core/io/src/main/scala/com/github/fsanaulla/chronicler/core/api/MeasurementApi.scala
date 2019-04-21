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

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, ResponseCode}
import com.github.fsanaulla.chronicler.core.either
import com.github.fsanaulla.chronicler.core.enums.{Consistency, Epoch, Precision}
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import com.github.fsanaulla.chronicler.core.typeclasses._

import scala.reflect.ClassTag

/**
  * Main functionality for measurement api
  * @tparam E - Entity type
  * @tparam R - Request entity type
  */
final class MeasurementApi[F[_], Req, Resp, Uri, Body, A](dbName: String,
                                                       measurementName: String,
                                                       gzipped: Boolean)
                                                      (implicit qb: QueryBuilder[Uri],
                                                       bd: BodyBuilder[Body],
                                                       re: RequestExecutor[F, Req, Resp, Uri, Body],
                                                       rh: ResponseHandler[Resp],
                                                       F: Functor[F]) extends DatabaseOperationQuery[Uri] with Appender {

  /**
    * Make single write
    * @param entity          - entity to write
    * @param consistency     - consistence level
    * @param precision       - time precision
    * @param retentionPolicy - retention policy type
    * @param writer          - implicit serializer to InfluxLine format
    * @return                - Write result on backend container
    */
  def write(entity: A,
            consistency: Option[Consistency] = None,
            precision: Option[Precision] = None,
            retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[A]): F[ErrorOr[ResponseCode]] = {
    val uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy)
    F.map(re.execute(uri, bd.fromT(measurementName, entity), gzipped))(rh.toWriteResult)
  }

  /**
    * Make bulk write
    * @param entities        - entities to write
    * @param consistency     - consistence level
    * @param precision       - time precision
    * @param retentionPolicy - retention policy type
    * @param writer          - implicit serializer to InfluxLine format
    * @return                - Write result on backend container
    */
  def bulkWrite(entities: Seq[A],
                consistency: Option[Consistency] = None,
                precision: Option[Precision] = None,
                retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[A]): F[ErrorOr[ResponseCode]] = {
    val uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy)
    F.map(re.execute(uri, bd.fromSeqT(measurementName, entities), gzipped))(rh.toWriteResult)
  }

  def read(query: String,
           epoch: Option[Epoch] = None,
           pretty: Boolean = false,
           chunked: Boolean = false)
          (implicit rd: InfluxReader[A], clsTag: ClassTag[A]): F[ErrorOr[Array[A]]] = {
    val uri = readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked)
    F.map(
      F.map(
        re.executeUri(uri))(rh.toQueryJsResult)
    ) { e =>
          e.flatMap { arr =>
            either.array[Throwable, A](arr.map(rd.read))
      }
    }
  }
}
