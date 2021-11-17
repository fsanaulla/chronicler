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

package com.github.fsanaulla.chronicler.core.management.db

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, ResponseCode}
import com.github.fsanaulla.chronicler.core.components._
import com.github.fsanaulla.chronicler.core.implicits._
import com.github.fsanaulla.chronicler.core.management.ManagementResponseHandler
import com.github.fsanaulla.chronicler.core.typeclasses.{FunctionK, Monad, MonadError}

/** Created by Author: fayaz.sanaulla@gmail.com Date: 08.08.17
  */
trait DatabaseManagement[F[_], G[_], Req, Resp, Uri, Body] extends DataManagementQuery[Uri] {
  implicit val qb: QueryBuilder[Uri]
  implicit val rb: RequestBuilder[Req, Uri, Body]
  implicit val re: RequestExecutor[F, Req, Resp]
  implicit val rh: ManagementResponseHandler[G, Resp]
  implicit val ME: MonadError[F, Throwable]
  implicit val FK: FunctionK[G, F]

  /** Create database
    *
    * @param dbName
    *   - database name
    * @param duration
    *   - database duration
    * @param replication
    *   - replication
    * @param shardDuration
    *   - shard duration
    * @param rpName
    *   - retention policy name
    * @return
    *   - execution R
    */
  final def createDatabase(
      dbName: String,
      duration: Option[String] = None,
      replication: Option[Int] = None,
      shardDuration: Option[String] = None,
      rpName: Option[String] = None
  ): F[ErrorOr[ResponseCode]] = {
    val uri  = createDatabaseQuery(dbName, duration, replication, shardDuration, rpName)
    val req  = rb.post(uri)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
  }

  /** Drop database */
  final def dropDatabase(dbName: String): F[ErrorOr[ResponseCode]] = {
    val uri  = dropDatabaseQuery(dbName)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
  }

  /** Drop measurement */
  final def dropMeasurement(dbName: String, measurementName: String): F[ErrorOr[ResponseCode]] = {
    val uri  = dropMeasurementQuery(dbName, measurementName)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
  }

  /** Show measurements */
  final def showMeasurement(
      dbName: String
  ): F[ErrorOr[Array[String]]] = {
    val uri  = showMeasurementQuery(dbName)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.queryResult[String](resp)))
  }

  /** Show database list */
  final def showDatabases: F[ErrorOr[Array[String]]] = {
    val req  = rb.get(showDatabasesQuery, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.queryResult[String](resp)))
  }

  /** Show field tags list */
  final def showFieldKeys(
      dbName: String,
      measurementName: String
  ): F[ErrorOr[Array[FieldInfo]]] = {
    val uri  = showFieldKeysQuery(dbName, measurementName)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.queryResult[FieldInfo](resp)))
  }

  /** Show tags keys list */
  final def showTagKeys(
      dbName: String,
      measurementName: String,
      whereClause: Option[String] = None,
      limit: Option[Int] = None,
      offset: Option[Int] = None
  ): F[ErrorOr[Array[String]]] = {
    val uri  = showTagKeysQuery(dbName, measurementName, whereClause, limit, offset)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.queryResult[String](resp)))
  }

  /** Show tag values list */
  final def showTagValues(
      dbName: String,
      measurementName: String,
      withKey: Seq[String],
      whereClause: Option[String] = None,
      limit: Option[Int] = None,
      offset: Option[Int] = None
  ): F[ErrorOr[Array[TagValue]]] = {
    val uri  = showTagValuesQuery(dbName, measurementName, withKey, whereClause, limit, offset)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.queryResult[TagValue](resp)))
  }
}
