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

  final def writeToInfluxQuery(dbName: String,
                               consistency: Option[Consistency],
                               precision: Option[Precision],
                               retentionPolicy: Option[String])
                              (implicit qb: QueryBuilder[U]): U = {

    val queryParams = scala.collection.mutable.Map[String, String]("db" -> dbName)
    for (rp <- retentionPolicy) queryParams += ("rp" -> rp)
    for (pr <- precision) queryParams += ("precision" -> pr.toString)
    for (cons <- consistency) queryParams += ("consistency" -> cons.toString)

    qb.buildQuery("/write", qb.withCredentials(queryParams))
  }

  final def readFromInfluxSingleQuery(dbName: String,
                                      query: String,
                                      epoch: Option[Epoch],
                                      pretty: Boolean,
                                      chunked: Boolean)
                                     (implicit qb: QueryBuilder[U]): U = {

    val queryParams = scala.collection.mutable.Map[String, String](
      "db" -> dbName,
      "q" -> query
    )

    for (ep <- epoch) queryParams += ("epoch" -> ep.toString)
    if (chunked) queryParams += ("chunked" -> chunked.toString)
    if (pretty) queryParams += ("pretty" -> pretty.toString)

    qb.buildQuery("/query", qb.withCredentials(queryParams))
  }

  final def readFromInfluxBulkQuery(dbName: String,
                                    queries: Seq[String],
                                    epoch: Option[Epoch],
                                    pretty: Boolean,
                                    chunked: Boolean)
                                   (implicit qb: QueryBuilder[U]): U = {
    val queryParams = mutable.Map[String, String](
      "db" -> dbName,
      "q" -> queries.mkString(";")
    )

    for (ep <- epoch) queryParams += ("epoch" -> ep.toString)
    if (chunked) queryParams += ("chunked" -> chunked.toString)
    if (pretty) queryParams += ("pretty" -> pretty.toString)

    qb.buildQuery("/query", qb.withCredentials(queryParams))
  }
}
