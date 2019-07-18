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

package com.github.fsanaulla.chronicler.core.management

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, ResponseCode}
import com.github.fsanaulla.chronicler.core.components._
import com.github.fsanaulla.chronicler.core.implicits._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.DataManagementQuery

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
trait DatabaseManagement[F[_], Resp, Uri, Body] extends DataManagementQuery[Uri] {
  implicit val qb: QueryBuilder[Uri]
  implicit val re: RequestExecutor[F, Resp, Uri, Body]
  implicit val rh: ResponseHandler[Resp]
  implicit val F: Functor[F]

  /**
    * Create database
    * @param dbName        - database name
    * @param duration      - database duration
    * @param replication   - replication
    * @param shardDuration - shard duration
    * @param rpName        - retention policy name
    * @return              - execution R
    */
  final def createDatabase(
      dbName: String,
      duration: Option[String] = None,
      replication: Option[Int] = None,
      shardDuration: Option[String] = None,
      rpName: Option[String] = None
    ): F[ErrorOr[ResponseCode]] =
    F.map(re.get(createDatabaseQuery(dbName, duration, replication, shardDuration, rpName)))(
      rh.writeResult
    )

  /** Drop database */
  final def dropDatabase(dbName: String): F[ErrorOr[ResponseCode]] =
    F.map(re.get(dropDatabaseQuery(dbName)))(rh.writeResult)

  /** Drop measurement */
  final def dropMeasurement(dbName: String, measurementName: String): F[ErrorOr[ResponseCode]] =
    F.map(re.get(dropMeasurementQuery(dbName, measurementName)))(rh.writeResult)

  /** Show measurements */
  final def showMeasurement(dbName: String): F[ErrorOr[Array[String]]] =
    F.map(re.get(showMeasurementQuery(dbName)))(rh.queryResult[String])

  /** Show database list */
  final def showDatabases(): F[ErrorOr[Array[String]]] =
    F.map(re.get(showDatabasesQuery))(rh.queryResult[String])

  /** Show field tags list */
  final def showFieldKeys(dbName: String, measurementName: String): F[ErrorOr[Array[FieldInfo]]] =
    F.map(re.get(showFieldKeysQuery(dbName, measurementName)))(rh.queryResult[FieldInfo])

  /** Show tags keys list */
  final def showTagKeys(
      dbName: String,
      measurementName: String,
      whereClause: Option[String] = None,
      limit: Option[Int] = None,
      offset: Option[Int] = None
    ): F[ErrorOr[Array[String]]] =
    F.map(re.get(showTagKeysQuery(dbName, measurementName, whereClause, limit, offset)))(
      rh.queryResult[String]
    )

  /** Show tag values list */
  final def showTagValues(
      dbName: String,
      measurementName: String,
      withKey: Seq[String],
      whereClause: Option[String] = None,
      limit: Option[Int] = None,
      offset: Option[Int] = None
    ): F[ErrorOr[Array[TagValue]]] =
    F.map(re.get(showTagValuesQuery(dbName, measurementName, withKey, whereClause, limit, offset)))(
      rh.queryResult[TagValue]
    )
}
