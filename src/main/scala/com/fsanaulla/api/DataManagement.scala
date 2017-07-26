package com.fsanaulla.api

import akka.http.scaladsl.model.HttpResponse
import com.fsanaulla.InfluxClient
import com.fsanaulla.model.DatabaseInfo
import com.fsanaulla.query.DataManagementQuery
import com.fsanaulla.utils.DataManagementHelper.toDatabaseInfo

import scala.concurrent.Future

trait DataManagement extends DataManagementQuery with RequestBuilder { self: InfluxClient =>

  def createDatabase(dbName: String): Future[HttpResponse] = {
    buildRequest(createDatabaseQuery(dbName))
  }

  def dropDatabase(dbName: String): Future[HttpResponse] = {
    buildRequest(dropDatabaseQuery(dbName))
  }

  def dropSeries(dbName: String, measurementName: String): Future[HttpResponse] = {
    buildRequest(dropSeriesQuery(dbName, measurementName))
  }

  def dropMeasurement(dbName: String, measurementName: String): Future[HttpResponse] = {
    buildRequest(dropMeasurementQuery(dbName, measurementName))
  }

  def deleteAllFromSeries(dbName: String, measurementName: String): Future[HttpResponse] = {
    buildRequest(deleteAllSeriesQuery(dbName, measurementName))
  }

  def dropShard(shardId: Int): Future[HttpResponse] = {
    buildRequest(dropShardQuery(shardId))
  }

  def showDatabases(): Future[Seq[DatabaseInfo]] = {
    buildRequest(showDatabasesQuery()).flatMap(toDatabaseInfo)
  }
}
