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
import com.github.fsanaulla.chronicler.core.components._
import com.github.fsanaulla.chronicler.core.either
import com.github.fsanaulla.chronicler.core.either._
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery

import scala.reflect.ClassTag

/**
  * Main functionality for measurement api
  */
class MeasurementApi[F[_], Resp, Uri, Body, A](
    dbName: String,
    measurementName: String,
    gzipped: Boolean
  )(implicit qb: QueryBuilder[Uri],
    bd: BodyBuilder[Body],
    re: RequestExecutor[F, Resp, Uri, Body],
    rh: ResponseHandler[Resp],
    F: Functor[F],
    FA: Failable[F])
  extends DatabaseOperationQuery[Uri] {

  /**
    * Make single write
    *
    * @param entity          - entity to write
    * @param consistency     - consistence level
    * @param precision       - time precision
    * @param retentionPolicy - retention policy type
    * @param wr          - implicit serializer to InfluxLine format
    * @return                - Write result on backend container
    */
  final def write(
      entity: A,
      consistency: Consistency = Consistencies.None,
      precision: Precision = Precisions.None,
      retentionPolicy: Option[String] = None
    )(implicit wr: InfluxWriter[A]
    ): F[ErrorOr[ResponseCode]] = {
    val uri = write(dbName, consistency, precision, retentionPolicy)

    bd.fromT(measurementName, entity) match {
      // fail fast
      case Left(ex) =>
        FA.fail(ex)
      case Right(body) =>
        F.map(re.post(uri, body, gzipped))(rh.writeResult)
    }
  }

  /**
    * Make bulk write
    *
    * @param entities        - entities to write
    * @param consistency     - consistence level
    * @param precision       - time precision
    * @param retentionPolicy - retention policy type
    * @param writer          - implicit serializer to InfluxLine format
    * @return                - Write result on backend container
    */
  final def bulkWrite(
      entities: Seq[A],
      consistency: Consistency = Consistencies.None,
      precision: Precision = Precisions.None,
      retentionPolicy: Option[String] = None
    )(implicit writer: InfluxWriter[A]
    ): F[ErrorOr[ResponseCode]] = {
    val uri = write(dbName, consistency, precision, retentionPolicy)

    bd.fromSeqT(measurementName, entities) match {
      // fail fast
      case Left(ex) =>
        FA.fail(ex)
      case Right(body) =>
        F.map(re.post(uri, body, gzipped))(rh.writeResult)
    }
  }

  final def read(
      query: String,
      epoch: Epoch = Epochs.None,
      pretty: Boolean = false
    )(implicit rd: InfluxReader[A],
      clsTag: ClassTag[A]
    ): F[ErrorOr[Array[A]]] = {
    val uri = singleQuery(dbName, query, epoch, pretty)
    F.map(
      F.map(re.get(uri))(rh.queryResultJson)
    ) { resp =>
      resp.flatMapRight { arr =>
        either.array[Throwable, A](arr.map(rd.read))
      }
    }
  }
}
