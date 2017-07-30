package com.fsanaulla.query

import akka.http.scaladsl.model.Uri

/**
  * Created by fayaz on 27.06.17.
  */
trait DataManagementQuery extends QueryBuilder {

  protected def createDatabaseQuery(dbName: String,
                                    duration: Option[String],
                                    replication: Option[String],
                                    shardDuration: Option[String],
                                    name: Option[String]): Uri = {
    val sb = StringBuilder.newBuilder

    sb.append(s"CREATE DATABASE $dbName")

    if (duration.isDefined || replication.isDefined || shardDuration.isDefined || name.isDefined) {
      sb.append(" WITH")
    }

    for (d <- duration) {
      sb.append(s" DURATION $d")
    }

    for (r <- replication) {
      sb.append(s" REPLICATION $r")
    }

    for (sd <- shardDuration) {
      sb.append(s" SHARD DURATION $sd")
    }

    for (n <- name) {
      sb.append(s" NAME $n")
    }

    queryBuilder("/query", sb.toString())
  }

  protected def dropDatabaseQuery(dbName: String): Uri = {
    queryBuilder("/query", s"DROP DATABASE $dbName")
  }

  protected def dropSeriesQuery(dbName: String, seriesName: String): Uri = {
    queryBuilder("/query", Map("db" -> dbName, "q" -> s"DROP SERIES FROM $seriesName"))
  }

  protected def dropMeasurementQuery(dbName: String, measurementName: String): Uri = {
    queryBuilder("/query", Map("db" -> dbName, "q" -> s"DROP MEASUREMENT $measurementName"))
  }

  protected def deleteAllSeriesQuery(dbName: String, seriesName: String): Uri = {
    queryBuilder("/query", Map("db" -> dbName, "q" -> s"DELETE FROM $seriesName"))
  }

  protected def dropShardQuery(shardId: Int): Uri = {
    queryBuilder("/query", s"DROP SHARD $shardId")
  }

  protected def showMeasurementQuery(dbName: String): Uri = {
    queryBuilder("/query", Map("db" -> dbName, "q" -> s"SHOW MEASUREMENTS"))
  }

  protected def showRetentionPoliciesQuery(dbName: String): Uri = {
    queryBuilder("/query", Map("db" -> dbName, "q" -> "SHOW RETENTION POLICIES"))
  }

  protected def showDatabasesQuery(): Uri = {
    queryBuilder("/query", s"SHOW DATABASES")
  }
}
