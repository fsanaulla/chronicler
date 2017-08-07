package com.fsanaulla.api

import com.fsanaulla.InfluxClient
import com.fsanaulla.model._
import com.fsanaulla.query.DataManagementQuery
import com.fsanaulla.utils.ResponseWrapper.{toQueryResult, toResult}

import scala.concurrent.Future

private[fsanaulla] trait DataManagement extends DataManagementQuery { self: InfluxClient =>

  def createDatabase(dbName: String,
                     duration: Option[String] = None,
                     replication: Option[String] = None,
                     shardDuration: Option[String] = None,
                     name: Option[String] = None): Future[Result] = {
    buildRequest(createDatabaseQuery(dbName, duration, replication, shardDuration, name)).flatMap(toResult)
  }

  def dropDatabase(dbName: String): Future[Result] = {
    buildRequest(dropDatabaseQuery(dbName)).flatMap(toResult)
  }

  def dropMeasurement(dbName: String, measurementName: String): Future[Result] = {
    buildRequest(dropMeasurementQuery(dbName, measurementName)).flatMap(toResult)
  }

  def dropShard(shardId: Int): Future[Result] = {
    buildRequest(dropShardQuery(shardId)).flatMap(toResult)
  }

  def showMeasurement(dbName: String)(implicit reader: InfluxReader[MeasurementInfo]): Future[QueryResult[MeasurementInfo]] = {
    buildRequest(showMeasurementQuery(dbName)).flatMap(toQueryResult[MeasurementInfo])
  }

  def showRetentionPolicies(dbName: String)(implicit reader: InfluxReader[RetentionPolicyInfo]): Future[QueryResult[RetentionPolicyInfo]] = {
    buildRequest(showRetentionPoliciesQuery(dbName)).flatMap(toQueryResult[RetentionPolicyInfo])
  }

  def showDatabases()(implicit reader: InfluxReader[DatabaseInfo]): Future[QueryResult[DatabaseInfo]] = {
    buildRequest(showDatabasesQuery())
      .flatMap(toQueryResult[DatabaseInfo])
      .map(res => res.copy(queryResult = res.queryResult.filterNot(_.dbName == "_internal")))
  }
}
