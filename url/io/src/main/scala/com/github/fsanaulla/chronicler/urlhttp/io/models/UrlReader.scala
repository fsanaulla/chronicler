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

package com.github.fsanaulla.chronicler.urlhttp.io.models

import com.github.fsanaulla.chronicler.core.enums.Epoch
import com.github.fsanaulla.chronicler.core.io.ReadOperations
import com.github.fsanaulla.chronicler.core.model.{HasCredentials, QueryResult, ReadResult}
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import com.github.fsanaulla.chronicler.urlhttp.shared.handlers.{UrlQueryBuilder, UrlRequestExecutor, UrlResponseHandler}
import com.softwaremill.sttp.Uri
import jawn.ast.JArray

import scala.util.Try

private[urlhttp] trait UrlReader
  extends UrlQueryBuilder
    with UrlRequestExecutor
    with UrlResponseHandler
    with DatabaseOperationQuery[Uri]
    with ReadOperations[Try] { self: HasCredentials =>

  private[chronicler] override def readJs(dbName: String,
                                          query: String,
                                          epoch: Epoch,
                                          pretty: Boolean,
                                          chunked: Boolean): Try[ReadResult[JArray]] = {
    val executionResult = execute(readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked))

    query match {
      case q: String if q.contains("GROUP BY") => executionResult.flatMap(toGroupedJsResult)
      case _ => executionResult.flatMap(toQueryJsResult)
    }
  }

  private[chronicler] override def bulkReadJs(dbName: String,
                                              queries: Seq[String],
                                              epoch: Epoch,
                                              pretty: Boolean,
                                              chunked: Boolean): Try[QueryResult[Array[JArray]]] = {
    val query = readFromInfluxBulkQuery(dbName, queries, epoch, pretty, chunked)
    execute(query).flatMap(toBulkQueryJsResult)
  }
}
