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
import com.github.fsanaulla.chronicler.core.either.EitherOps
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import com.github.fsanaulla.chronicler.core.typeclasses.{FunctionK, Functor, MonadError}

import scala.reflect.ClassTag

/** Main functionality for measurement api
  */
class MeasurementApi[F[_], G[_], Req, Uri, Body, Resp, A](
    dbName: String,
    measurementName: String,
    gzipped: Boolean
)(implicit
    qb: QueryBuilder[Uri],
    bd: BodyBuilder[Body],
    rb: RequestBuilder[Req, Uri, Body],
    re: RequestExecutor[F, Req, Resp],
    rh: ResponseHandlerBase[G, Resp],
    F: Functor[F],
    ME: MonadError[F, Throwable],
    FK: FunctionK[G, F]
) extends DatabaseOperationQuery[Uri] {

  /** Make single write
    *
    * @param entity
    *   - entity to write
    * @param consistency
    *   - consistence level
    * @param precision
    *   - time precision
    * @param retentionPolicy
    *   - retention policy type
    * @param wr
    *   - implicit serializer to InfluxLine format
    * @return
    *   - Write result on backend container
    */
  final def write(
      entity: A,
      consistency: Consistency = Consistencies.None,
      precision: Precision = Precisions.None,
      retentionPolicy: Option[String] = None
  )(implicit wr: InfluxWriter[A]): F[ErrorOr[ResponseCode]] = {
    val uri  = write(dbName, consistency, precision, retentionPolicy)
    val body = bd.fromT(measurementName, entity)
    val req = body match {
      case Left(ex)     => ME.fail(ex)
      case Right(value) => ME.pure(rb.post(uri, value, gzipped))
    }
    val resp = ME.flatMap(req)(re.execute)

    ME.flatMap(resp)(r => FK(rh.writeResult(r)))
  }

  /** Make bulk write
    *
    * @param entities
    *   - entities to write
    * @param consistency
    *   - consistence level
    * @param precision
    *   - time precision
    * @param retentionPolicy
    *   - retention policy type
    * @param writer
    *   - implicit serializer to InfluxLine format
    * @return
    *   - Write result on backend container
    */
  final def bulkWrite(
      entities: Seq[A],
      consistency: Consistency = Consistencies.None,
      precision: Precision = Precisions.None,
      retentionPolicy: Option[String] = None
  )(implicit writer: InfluxWriter[A]): F[ErrorOr[ResponseCode]] = {
    val uri  = write(dbName, consistency, precision, retentionPolicy)
    val body = bd.fromSeqT(measurementName, entities)
    val req = body match {
      case Left(ex)     => ME.fail(ex)
      case Right(value) => ME.pure(rb.post(uri, value, gzipped))
    }
    val resp = ME.flatMap(req)(re.execute)

    ME.flatMap(resp)(r => FK(rh.writeResult(r)))
  }

  final def read(
      query: String,
      epoch: Epoch = Epochs.None,
      pretty: Boolean = false
  )(implicit rd: InfluxReader[A], clsTag: ClassTag[A]): F[ErrorOr[Array[A]]] = {
    val uri  = singleQuery(dbName, query, epoch, pretty)
    val req  = rb.get(uri, gzipped)
    val resp = re.execute(req)

    ME.flatMap(resp) { r =>
      F.map(FK(rh.queryResultJson(r))) { ethResp =>
        ethResp.flatMapRight { arr =>
          either.array(arr.map(rd.read))
        }
      }
    }
  }
}
