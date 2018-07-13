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

import com.github.fsanaulla.chronicler.core.handlers.QueryHandler
import com.github.fsanaulla.chronicler.core.model.HasCredentials

import scala.collection.mutable

private[chronicler] trait RetentionPolicyManagementQuery[U] {
  self: QueryHandler[U] with HasCredentials =>

  private[chronicler] final def createRetentionPolicyQuery(rpName: String,
                                                           dbName: String,
                                                           duration: String,
                                                           replication: Int,
                                                           shardDuration: Option[String],
                                                           default: Boolean = false): U = {
    val sb = StringBuilder.newBuilder

    sb.append("CREATE RETENTION POLICY ")
      .append(rpName)
      .append(" ON ")
      .append(dbName)
      .append(" DURATION ")
      .append(duration)
      .append(" REPLICATION ")
      .append(replication)

    for (sd <- shardDuration) {
      sb.append(" SHARD DURATION ").append(sd)
    }

    if (default) sb.append(" DEFAULT")

    buildQuery("/query", buildQueryParams(sb.toString()))
  }

  private[chronicler] final def dropRetentionPolicyQuery(rpName: String, dbName: String): U =
    buildQuery("/query", buildQueryParams(s"DROP RETENTION POLICY $rpName ON $dbName"))

  private[chronicler] final def updateRetentionPolicyQuery(rpName: String,
                                                           dbName: String,
                                                           duration: Option[String],
                                                           replication: Option[Int],
                                                           shardDuration: Option[String],
                                                           default: Boolean = false): U = {
    val sb = StringBuilder.newBuilder

    sb.append("ALTER RETENTION POLICY ")
      .append(rpName)
      .append(" ON ")
      .append(dbName)

    for (d <- duration) {
      sb.append(" DURATION ").append(d)
    }

    for (r <- replication) {
      sb.append(" REPLICATION ").append(r)
    }

    for (sd <- shardDuration) {
      sb.append(" SHARD DURATION ").append(sd)
    }

    if (default) sb.append(" DEFAULT")

    buildQuery("/query", buildQueryParams(sb.toString()))
  }

  private[chronicler] final def showRetentionPoliciesQuery(dbName: String): U =
    buildQuery("/query", buildQueryParams(mutable.Map("db" -> dbName, "q" -> "SHOW RETENTION POLICIES")))
}
