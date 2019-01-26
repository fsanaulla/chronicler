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
import com.github.fsanaulla.chronicler.core.query.QueriesManagementQuery
import com.github.fsanaulla.chronicler.core.typeclasses.{FlatMap, QueryBuilder, RequestExecutor, ResponseHandler}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[chronicler] trait QueriesManagement[F[_], Req, Resp, Uri, Entity] extends QueriesManagementQuery[Uri] {
  self: RequestExecutor[F, Req, Resp, Uri]
    with ResponseHandler[F, Resp]
    with QueryBuilder[Uri]
    with FlatMap[F]
    with HasCredentials =>

  /** Show list of queries */
  final def showQueries: F[QueryResult[QueryInfo]] =
    flatMap(execute(showQuerysQuery))(toQueryResult[QueryInfo])

  /** Kill query */
  final def killQuery(queryId: Int): F[WriteResult] =
    flatMap(execute(killQueryQuery(queryId)))(toResult)
}
