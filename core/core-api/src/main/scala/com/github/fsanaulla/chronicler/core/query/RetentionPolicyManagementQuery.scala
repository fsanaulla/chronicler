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
