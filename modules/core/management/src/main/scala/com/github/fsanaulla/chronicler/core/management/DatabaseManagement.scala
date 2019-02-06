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

import com.github.fsanaulla.chronicler.core.implicits._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.DataManagementQuery
import com.github.fsanaulla.chronicler.core.typeclasses.{FlatMap, QueryBuilder, RequestExecutor, ResponseHandler}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[chronicler] trait DatabaseManagement[F[_], Req, Resp, Uri, Entity] extends DataManagementQuery[Uri] {
  self: RequestExecutor[F, Req, Resp, Uri]
    with ResponseHandler[F, Resp]
    with QueryBuilder[Uri]
    with FlatMap[F]
    with HasCredentials =>

  /**
    * Create database
    * @param dbName        - database name
    * @param duration      - database duration
    * @param replication   - replication
    * @param shardDuration - shard duration
    * @param rpName        - retention policy name
    * @return              - execution R
    */
  final def createDatabase(dbName: String,
                           duration: Option[String] = None,
                           replication: Option[Int] = None,
                           shardDuration: Option[String] = None,
                           rpName: Option[String] = None): F[WriteResult] =
    flatMap(execute(createDatabaseQuery(dbName, duration, replication, shardDuration, rpName)))(toResult)


  /** Drop database */
  final def dropDatabase(dbName: String): F[WriteResult] =
    flatMap(execute(dropDatabaseQuery(dbName)))(toResult)

  /** Drop measurement */
  final def dropMeasurement(dbName: String, measurementName: String): F[WriteResult] =
    flatMap(execute(dropMeasurementQuery(dbName, measurementName)))( toResult)

  /** Show measurements */
  final def showMeasurement(dbName: String): F[QueryResult[String]] =
    flatMap(execute(showMeasurementQuery(dbName)))(toQueryResult[String])

  /** Show database list */
  final def showDatabases(): F[QueryResult[String]] =
    flatMap(execute(showDatabasesQuery))(toQueryResult[String])

  /** Show field tags list */
  final def showFieldKeys(dbName: String, measurementName: String): F[QueryResult[FieldInfo]] =
    flatMap(execute(showFieldKeysQuery(dbName, measurementName)))(toQueryResult[FieldInfo])

  /** Show tags keys list */
  final def showTagKeys(dbName: String, 
                        measurementName: String, 
                        whereClause: Option[String] = None, 
                        limit: Option[Int] = None, 
                        offset: Option[Int] = None): F[QueryResult[String]] =
    flatMap(execute(showTagKeysQuery(dbName, measurementName, whereClause, limit, offset)))(toQueryResult[String])

  /** Show tag values list */
  final def showTagValues(dbName: String, 
                          measurementName: String, 
                          withKey: Seq[String], 
                          whereClause: Option[String] = None, 
                          limit: Option[Int] = None, 
                          offset: Option[Int] = None): F[QueryResult[TagValue]] =
    flatMap(execute(showTagValuesQuery(dbName, measurementName, withKey, whereClause, limit, offset)))(toQueryResult[TagValue])
}
