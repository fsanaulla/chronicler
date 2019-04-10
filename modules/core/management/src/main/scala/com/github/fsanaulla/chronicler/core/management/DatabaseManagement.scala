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

  implicit val qb: QueryBuilder[Uri]
  implicit val re: RequestExecutor[F, Req, Resp, Uri]
  implicit val rh: ResponseHandler[F, Resp]
  implicit val fm: FlatMap[F]

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
    fm.flatMap(re.executeRequest(createDatabaseQuery(dbName, duration, replication, shardDuration, rpName)))(rh.toWriteResult)


  /** Drop database */
  final def dropDatabase(dbName: String): F[WriteResult] =
    fm.flatMap(re.executeRequest(dropDatabaseQuery(dbName)))(rh.toWriteResult)

  /** Drop measurement */
  final def dropMeasurement(dbName: String, measurementName: String): F[WriteResult] =
    fm.flatMap(re.executeRequest(dropMeasurementQuery(dbName, measurementName)))(rh.toWriteResult)

  /** Show measurements */
  final def showMeasurement(dbName: String): F[QueryResult[String]] =
    fm.flatMap(re.executeRequest(showMeasurementQuery(dbName)))(rh.toQueryResult[String])

  /** Show database list */
  final def showDatabases(): F[QueryResult[String]] =
    fm.flatMap(re.executeRequest(showDatabasesQuery))(rh.toQueryResult[String])

  /** Show field tags list */
  final def showFieldKeys(dbName: String, measurementName: String): F[QueryResult[FieldInfo]] =
    fm.flatMap(re.executeRequest(showFieldKeysQuery(dbName, measurementName)))(rh.toQueryResult[FieldInfo])

  /** Show tags keys list */
  final def showTagKeys(dbName: String, 
                        measurementName: String, 
                        whereClause: Option[String] = None, 
                        limit: Option[Int] = None, 
                        offset: Option[Int] = None): F[QueryResult[String]] =
    fm.flatMap(re.executeRequest(showTagKeysQuery(dbName, measurementName, whereClause, limit, offset)))(rh.toQueryResult[String])

  /** Show tag values list */
  final def showTagValues(dbName: String, 
                          measurementName: String, 
                          withKey: Seq[String], 
                          whereClause: Option[String] = None, 
                          limit: Option[Int] = None, 
                          offset: Option[Int] = None): F[QueryResult[TagValue]] =
    fm.flatMap(re.executeRequest(showTagValuesQuery(dbName, measurementName, withKey, whereClause, limit, offset)))(rh.toQueryResult[TagValue])
}
