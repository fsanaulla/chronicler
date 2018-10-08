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

import com.github.fsanaulla.chronicler.core.handlers.ResponseHandler
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.ContinuousQueries
import com.github.fsanaulla.chronicler.core.typeclasses.{FlatMap, QueryBuilder, RequestExecutor}
import com.github.fsanaulla.chronicler.core.utils.DefaultInfluxImplicits._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[chronicler] trait ContinuousQueryManagement[F[_], Req, Resp, Uri, Entity] extends ContinuousQueries[Uri] {
  self: RequestExecutor[F, Req, Resp, Uri]
    with ResponseHandler[F, Resp]
    with FlatMap[F]
    with QueryBuilder[Uri]
    with HasCredentials =>

  /**
    * Create new one continuous query
    *
    * @param dbName - database on which CQ will runes
    * @param cqName - continuous query name
    * @param query  - query
    * @return
    */
  final def createCQ(dbName: String, cqName: String, query: String): F[WriteResult] = {
    require(validCQQuery(query), "Query required INTO and GROUP BY clause")
    flatMap(execute(createCQQuery(dbName, cqName, query)))(toResult)
  }

  /** Show continuous query information */
  final def showCQs: F[QueryResult[ContinuousQueryInfo]] =
    flatMap(execute(showCQQuery))(toCqQueryResult)

  /**
    * Drop continuous query
    *
    * @param dbName - database name
    * @param cqName - continuous query name
    * @return       - execution result
    */
  final def dropCQ(dbName: String, cqName: String): F[WriteResult] =
    flatMap(execute(dropCQQuery(dbName, cqName)))(toResult)

  private def validCQQuery(query: String): Boolean =
    if (query.contains("INTO") && query.contains("GROUP BY")) true else false

}
