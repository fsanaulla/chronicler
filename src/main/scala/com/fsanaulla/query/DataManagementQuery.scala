package com.fsanaulla.query

import akka.http.scaladsl.model.Uri

/**
  * Created by fayaz on 27.06.17.
  */
trait DataManagementQuery extends QueryBuilder {

  protected def createDatabaseQuery(dbName: String): Uri = {
    queryBuilder("/query", s"CREATE DATABASE $dbName")
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

  protected def showDatabasesQuery(): Uri = {
    queryBuilder("/query", s"SHOW DATABASES")
  }
}
