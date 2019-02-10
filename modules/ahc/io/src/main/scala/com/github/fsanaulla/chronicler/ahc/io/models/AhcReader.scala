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

package com.github.fsanaulla.chronicler.ahc.io.models

import com.github.fsanaulla.chronicler.ahc.shared.alias.Request
import com.github.fsanaulla.chronicler.core.enums.Epoch
import com.github.fsanaulla.chronicler.core.io.ReadOperations
import com.github.fsanaulla.chronicler.core.model.{QueryResult, ReadResult}
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import com.github.fsanaulla.chronicler.core.typeclasses.{QueryBuilder, RequestExecutor, ResponseHandler}
import com.softwaremill.sttp.{Response, Uri}
import jawn.ast.{JArray, JValue}

import scala.concurrent.{ExecutionContext, Future}

private[ahc] final class AhcReader(implicit qb: QueryBuilder[Uri],
                                   re: RequestExecutor[Future, Request, Response[JValue], Uri],
                                   rh: ResponseHandler[Future, Response[JValue]],
                                   ec: ExecutionContext)
  extends DatabaseOperationQuery[Uri] with ReadOperations[Future] {

  private[chronicler] override def readJs(dbName: String,
                                          query: String,
                                          epoch: Option[Epoch],
                                          pretty: Boolean,
                                          chunked: Boolean): Future[ReadResult[JArray]] = {
    val uri = readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked)
    val executionResult = re.execute(re.buildRequest(uri))
    query match {
      case q: String if q.contains("GROUP BY") =>
        executionResult.flatMap(rh.toGroupedJsResult)
      case _ =>
        executionResult.flatMap(rh.toQueryJsResult)
    }
  }

  private[chronicler] override def bulkReadJs(dbName: String,
                                              queries: Seq[String],
                                              epoch: Option[Epoch],
                                              pretty: Boolean,
                                              chunked: Boolean): Future[QueryResult[Array[JArray]]] = {
    val uri = readFromInfluxBulkQuery(dbName, queries, epoch, pretty, chunked)
    re.execute(re.buildRequest(uri)).flatMap(rh.toBulkQueryJsResult)
  }
}
