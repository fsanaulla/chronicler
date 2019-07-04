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

package com.github.fsanaulla.chronicler.core.query

import com.github.fsanaulla.chronicler.core.components.QueryBuilder
import com.github.fsanaulla.chronicler.core.enums.{Consistency, Epoch, Precision}

import scala.collection.mutable

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
trait DatabaseOperationQuery[U] {

  private[chronicler]
  final def writeToInfluxQuery(dbName: String,
                               consistency: Consistency,
                               precision: Precision,
                               retentionPolicy: Option[String])
                              (implicit qb: QueryBuilder[U]): U = {

    val queryParams = scala.collection.mutable.Map[String, String]("db" -> dbName)

    for (rp <- retentionPolicy) queryParams += ("rp" -> rp)

    if (!precision.isNone)
      queryParams += ("precision" -> precision.toString)

    if (!consistency.isNone)
      queryParams += ("consistency" -> consistency.toString)

    qb.buildQuery("/write", qb.withCredentials(queryParams))
  }

  private[chronicler]
  final def readFromInfluxSingleQuery(dbName: String,
                                      query: String,
                                      epoch: Epoch,
                                      pretty: Boolean,
                                      chunkSize: Int = -1)
                                     (implicit qb: QueryBuilder[U]): U = {

    val queryParams = scala.collection.mutable.Map[String, String](
      "db" -> dbName,
      "q" -> query
    )

    if (!epoch.isNone) queryParams += ("epoch" -> epoch.toString)

    if (chunkSize != -1) {
      queryParams += "chunked" -> String.valueOf(true)
      queryParams += "chunk_size" -> String.valueOf(chunkSize)
    }

    if (pretty) queryParams += ("pretty" -> pretty.toString)

    qb.buildQuery("/query", qb.withCredentials(queryParams))
  }

  private[chronicler]
  final def readFromInfluxBulkQuery(dbName: String,
                                    queries: Seq[String],
                                    epoch: Epoch,
                                    pretty: Boolean)
                                   (implicit qb: QueryBuilder[U]): U = {
    val queryParams = mutable.Map[String, String](
      "db" -> dbName,
      "q" -> queries.mkString(";")
    )

    if (!epoch.isNone) queryParams += ("epoch" -> epoch.toString)
    if (pretty) queryParams += ("pretty" -> pretty.toString)

    qb.buildQuery("/query", qb.withCredentials(queryParams))
  }
}
