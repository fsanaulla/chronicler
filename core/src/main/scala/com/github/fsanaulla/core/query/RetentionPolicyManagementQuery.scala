package com.github.fsanaulla.core.query

import com.github.fsanaulla.core.handlers.QueryHandler
import com.github.fsanaulla.core.model.{HasCredentials, InfluxCredentials}

import scala.collection.mutable

private[fsanaulla] trait RetentionPolicyManagementQuery[U]  {
  self: QueryHandler[U] with HasCredentials =>

  def createRetentionPolicyQuery(rpName: String,
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

  def dropRetentionPolicyQuery(rpName: String, dbName: String): U = {
    buildQuery("/query", buildQueryParams(s"DROP RETENTION POLICY $rpName ON $dbName"))
  }

  def updateRetentionPolicyQuery(rpName: String,
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

  def showRetentionPoliciesQuery(dbName: String): U = {
    buildQuery("/query", buildQueryParams(mutable.Map("db" -> dbName, "q" -> "SHOW RETENTION POLICIES")))
  }
}
