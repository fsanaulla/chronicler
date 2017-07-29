package com.fsanaulla.api

import akka.http.scaladsl.model.HttpResponse
import com.fsanaulla.InfluxClient
import com.fsanaulla.model.{DatabaseInfo, MeasurementInfo, RetentionPolicyInfo}
import com.fsanaulla.query.DataManagementQuery
import com.fsanaulla.utils.DataManagementHelper._

import scala.concurrent.Future

trait DataManagement
  extends DataManagementQuery
    with RequestBuilder { self: InfluxClient =>

  def createDatabase(dbName: String,
                     duration: Option[String] = None,
                     replication: Option[String] = None,
                     shardDuration: Option[String] = None,
                     name: Option[String] = None
                    ): Future[HttpResponse] = {
    buildRequest(createDatabaseQuery(dbName, duration, replication, shardDuration, name))
  }

  def dropDatabase(dbName: String): Future[HttpResponse] = {
    buildRequest(dropDatabaseQuery(dbName))
  }

  def dropMeasurement(dbName: String, measurementName: String): Future[HttpResponse] = {
    buildRequest(dropMeasurementQuery(dbName, measurementName))
  }

  def dropShard(shardId: Int): Future[HttpResponse] = {
    buildRequest(dropShardQuery(shardId))
  }

  def showMeasurement(dbName: String): Future[Seq[MeasurementInfo]] = {
    buildRequest(showMeasurementQuery(dbName)).flatMap(toMeasurementInfo)
  }

  def showRetentionPolicies(dbName: String): Future[Seq[RetentionPolicyInfo]] = {
    buildRequest(showRetentionPoliciesQuery(dbName)).flatMap(toRetentionPolicy)
  }

  def showDatabases(): Future[Seq[DatabaseInfo]] = {
    buildRequest(showDatabasesQuery()).flatMap(toDatabaseInfo)
  }

  //todo: test
//  def deleteAllFromSeries(dbName: String, measurementName: String): Future[HttpResponse] = {
//    buildRequest(deleteAllSeriesQuery(dbName, measurementName))
//  }
//
//  def dropSeries(dbName: String, measurementName: String): Future[HttpResponse] = {
//    buildRequest(dropSeriesQuery(dbName, measurementName))
//  }
}
