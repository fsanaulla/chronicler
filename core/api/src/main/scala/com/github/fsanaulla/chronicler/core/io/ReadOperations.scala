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

package com.github.fsanaulla.chronicler.core.io

import com.github.fsanaulla.chronicler.core.enums.Epoch
import com.github.fsanaulla.chronicler.core.model.{QueryResult, ReadResult}
import jawn.ast.JArray

/***
  * Trait that define main IO operation for working with DB.
  */
trait ReadOperations[F[_]] {

  /**
    * Execute single query from InfluxDB
    *
    * @param dbName  - For which database
    * @param query   - SQL based query
    * @param epoch   - Epochs interval
    * @param pretty  - Flag for enabling/disabling JSON pretty printing
    * @param chunked - Chunked response
    * @return        - Query result, array of JArray
    */
  private[chronicler] def readJs(dbName: String, query: String, epoch: Epoch, pretty: Boolean, chunked: Boolean): F[ReadResult[JArray]]

  /**
    * Execute several queries in one time
    *
    * @param dbName  - For which database
    * @param queries - SQL based queries, that must be executed
    * @param epoch   - Epochs interval
    * @param pretty  - Flag for enabling/disabling JSON pretty printing
    * @param chunked - Chunked response
    * @return        - Multiple query results, array of JArray
    */
  private[chronicler] def bulkReadJs(dbName: String, queries: Seq[String], epoch: Epoch, pretty: Boolean, chunked: Boolean): F[QueryResult[Array[JArray]]]

}
