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

package com.github.fsanaulla.chronicler.core.api.management

import com.github.fsanaulla.chronicler.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.DataManagementQuery
import com.github.fsanaulla.chronicler.core.utils.DefaultInfluxImplicits._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[chronicler] trait DatabaseManagement[M[_], Req, Resp, Uri, Entity] extends DataManagementQuery[Uri] {
  self: RequestHandler[M, Req, Resp, Uri]
    with ResponseHandler[M, Resp]
    with QueryHandler[Uri]
    with Mappable[M, Resp]
    with ImplicitRequestBuilder[Uri, Req]
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
                           rpName: Option[String] = None): M[WriteResult] =
    mapTo(execute(createDatabaseQuery(dbName, duration, replication, shardDuration, rpName)), toResult)


  /** Drop database */
  final def dropDatabase(dbName: String): M[WriteResult] =
    mapTo(execute(dropDatabaseQuery(dbName)), toResult)

  /** Drop measurement */
  final def dropMeasurement(dbName: String, measurementName: String): M[WriteResult] =
    mapTo(execute(dropMeasurementQuery(dbName, measurementName)), toResult)

  /** Show measurements */
  final def showMeasurement(dbName: String): M[QueryResult[String]] =
    mapTo(execute(showMeasurementQuery(dbName)), toQueryResult[String])

  /** Show database list */
  final def showDatabases(): M[QueryResult[String]] =
    mapTo(execute(showDatabasesQuery()), toQueryResult[String])

  /** Show field tags list */
  final def showFieldKeys(dbName: String, measurementName: String): M[QueryResult[FieldInfo]] =
    mapTo(execute(showFieldKeysQuery(dbName, measurementName)), toQueryResult[FieldInfo])

  /** Show tags keys list */
  final def showTagKeys(dbName: String, 
                        measurementName: String, 
                        whereClause: Option[String] = None, 
                        limit: Option[Int] = None, 
                        offset: Option[Int] = None): M[QueryResult[String]] =
    mapTo(execute(showTagKeysQuery(dbName, measurementName, whereClause, limit, offset)), toQueryResult[String])

  /** Show tag values list */
  final def showTagValues(dbName: String, 
                          measurementName: String, 
                          withKey: Seq[String], 
                          whereClause: Option[String] = None, 
                          limit: Option[Int] = None, 
                          offset: Option[Int] = None): M[QueryResult[TagValue]] =
    mapTo(execute(showTagValuesQuery(dbName, measurementName, withKey, whereClause, limit, offset)), toQueryResult[TagValue])
}
