package com.github.fsanaulla.query

import com.github.fsanaulla.handlers.QueryHandler
import com.github.fsanaulla.model.InfluxCredentials

private[fsanaulla] trait RetentionPolicyManagementQuery[U]  { self: QueryHandler[U] =>

  protected def createRetentionPolicyQuery(rpName: String,
                                           dbName: String,
                                           duration: String,
                                           replication: Int,
                                           shardDuration: Option[String],
                                           default: Boolean = false)
                                          (implicit credentials: InfluxCredentials): U = {
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

  protected def dropRetentionPolicyQuery(rpName: String,
                                         dbName: String)
                                        (implicit credentials: InfluxCredentials): U = {
    buildQuery("/query", buildQueryParams(s"DROP RETENTION POLICY $rpName ON $dbName"))
  }

  protected def updateRetentionPolicyQuery(rpName: String,
                                           dbName: String,
                                           duration: Option[String],
                                           replication: Option[Int],
                                           shardDuration: Option[String],
                                           default: Boolean = false)
                                          (implicit credentials: InfluxCredentials): U = {
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
}
