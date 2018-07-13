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

package com.github.fsanaulla.chronicler.core.query

import com.github.fsanaulla.chronicler.core.enums.{Consistency, Epoch, Precision}
import com.github.fsanaulla.chronicler.core.handlers.QueryHandler
import com.github.fsanaulla.chronicler.core.model.HasCredentials

import scala.collection.mutable

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
private[chronicler] trait DatabaseOperationQuery[U] {
  self: QueryHandler[U] with HasCredentials =>

  private[chronicler] final def writeToInfluxQuery(dbName: String,
                                                   consistency: Consistency,
                                                   precision: Precision,
                                                   retentionPolicy: Option[String]): U = {

    val queryParams = scala.collection.mutable.Map[String, String](
      "db" -> dbName,
      "consistency" -> consistency.toString,
      "precision" -> precision.toString
    )

    for (rp <- retentionPolicy) {
      queryParams += ("rp" -> rp)
    }

    buildQuery("/write", buildQueryParams(queryParams))
  }

  private[chronicler] final def readFromInfluxSingleQuery(dbName: String,
                                                          query: String,
                                                          epoch: Epoch,
                                                          pretty: Boolean,
                                                          chunked: Boolean): U = {

    val queryParams = scala.collection.mutable.Map[String, String](
      "db" -> dbName,
      "pretty" -> pretty.toString,
      "chunked" -> chunked.toString,
      "epoch" -> epoch.toString,
      "q" -> query
    )

    buildQuery("/query", buildQueryParams(queryParams))
  }

  private[chronicler] final def readFromInfluxBulkQuery(dbName: String,
                                                        queries: Seq[String],
                                                        epoch: Epoch,
                                                        pretty: Boolean,
                                                        chunked: Boolean): U = {
    val queryParams = mutable.Map[String, String](
      "db" -> dbName,
      "pretty" -> pretty.toString,
      "chunked" -> chunked.toString,
      "epoch" -> epoch.toString,
      "q" -> queries.mkString(";")
    )

    buildQuery("/query", buildQueryParams(queryParams))
  }
}
