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

package com.github.fsanaulla.chronicler.core.management.cq

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, ResponseCode}
import com.github.fsanaulla.chronicler.core.components.{
  QueryBuilder,
  RequestBuilder,
  RequestExecutor
}
import com.github.fsanaulla.chronicler.core.management.ManagementResponseHandler
import com.github.fsanaulla.chronicler.core.typeclasses.{FunctionK, MonadError}

/** Created by Author: fayaz.sanaulla@gmail.com Date: 08.08.17
  */
trait ContinuousQueryManagement[F[_], G[_], Req, Resp, U, E] extends ContinuousQueries[U] {
  implicit val qb: QueryBuilder[U]
  implicit val rb: RequestBuilder[Req, U, E]
  implicit val re: RequestExecutor[F, Req, Resp]
  implicit val rh: ManagementResponseHandler[G, Resp]
  implicit val ME: MonadError[F, Throwable]
  implicit val FK: FunctionK[G, F]

  /** Create new one continuous query
    *
    * @param dbName
    *   - database on which CQ will runes
    * @param cqName
    *   - continuous query name
    * @param query
    *   - query
    * @return
    */
  final def createCQ(
      dbName: String,
      cqName: String,
      query: String
  ): F[ErrorOr[ResponseCode]] = {
    if (!validCQQuery(query)) ME.fail(new Exception("Query required INTO and GROUP BY clause"))
    else {
      val uri  = createCQQuery(dbName, cqName, query)
      val req  = rb.get(uri, compress = false)
      val resp = re.execute(req)

      ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
    }
  }

  /** Show continuous query information */
  final def showCQs: F[ErrorOr[Array[ContinuousQueryInfo]]] = {
    val req  = rb.get(showCQQuery, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.toCqQueryResult(resp)))
  }

  /** Drop continuous query
    *
    * @param dbName
    *   - database name
    * @param cqName
    *   - continuous query name
    * @return
    *   - execution result
    */
  final def dropCQ(dbName: String, cqName: String): F[ErrorOr[ResponseCode]] = {
    val uri  = dropCQQuery(dbName, cqName)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
  }

  private[this] def validCQQuery(query: String): Boolean =
    query.contains("INTO") && query.contains("GROUP BY")
}
