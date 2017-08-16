package com.fsanaulla.query

import akka.http.scaladsl.model.Uri
import com.fsanaulla.model.InfluxCredentials

import scala.collection.mutable

/**
  * Created by fayaz on 27.06.17.
  */
private[fsanaulla] trait DataManagementQuery extends QueryBuilder {

  protected def createDatabaseQuery(dbName: String,
                                    duration: Option[String],
                                    replication: Option[Int],
                                    shardDuration: Option[String],
                                    rpName: Option[String])(implicit credentials: InfluxCredentials): Uri = {
    val sb = StringBuilder.newBuilder

    sb.append(s"CREATE DATABASE $dbName")

    if (duration.isDefined || replication.isDefined || shardDuration.isDefined || rpName.isDefined) {
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

    for (rp <- rpName) {
      sb.append(s" NAME $rp")
    }

    queryBuilder("/query", buildQueryParams(sb.toString()))
  }

  protected def dropDatabaseQuery(dbName: String)(implicit credentials: InfluxCredentials): Uri = {
    queryBuilder("/query", buildQueryParams(s"DROP DATABASE $dbName"))
  }

  protected def dropSeriesQuery(dbName: String, seriesName: String)(implicit credentials: InfluxCredentials): Uri = {
    queryBuilder("/query", buildQueryParams(mutable.Map("db" -> dbName, "q" -> s"DROP SERIES FROM $seriesName")))
  }

  protected def dropMeasurementQuery(dbName: String, measurementName: String)(implicit credentials: InfluxCredentials): Uri = {
    queryBuilder("/query", buildQueryParams(mutable.Map("db" -> dbName, "q" -> s"DROP MEASUREMENT $measurementName")))
  }

  protected def deleteAllSeriesQuery(dbName: String, seriesName: String)(implicit credentials: InfluxCredentials): Uri = {
    queryBuilder("/query", buildQueryParams(mutable.Map("db" -> dbName, "q" -> s"DELETE FROM $seriesName")))
  }

  protected def dropShardQuery(shardId: Int)(implicit credentials: InfluxCredentials): Uri = {
    queryBuilder("/query", buildQueryParams(s"DROP SHARD $shardId"))
  }

  protected def showMeasurementQuery(dbName: String)(implicit credentials: InfluxCredentials): Uri = {
    queryBuilder("/query", buildQueryParams(mutable.Map("db" -> dbName, "q" -> s"SHOW MEASUREMENTS")))
  }

  protected def showRetentionPoliciesQuery(dbName: String)(implicit credentials: InfluxCredentials): Uri = {
    queryBuilder("/query", buildQueryParams(mutable.Map("db" -> dbName, "q" -> "SHOW RETENTION POLICIES")))
  }

  protected def showDatabasesQuery()(implicit credentials: InfluxCredentials): Uri = {
    queryBuilder("/query", buildQueryParams(s"SHOW DATABASES"))
  }
}
