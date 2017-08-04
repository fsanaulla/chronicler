package com.fsanaulla.api

import com.fsanaulla.InfluxClient
import com.fsanaulla.model._
import com.fsanaulla.query.DataManagementQuery
import com.fsanaulla.utils.Helper._
import com.fsanaulla.utils.ResponseWrapper.{toQueryResult, toResult}

import scala.concurrent.Future

private[fsanaulla] trait DataManagement extends DataManagementQuery { self: InfluxClient =>

  //SYNCHRONOUS API
  def createDatabaseSync(dbName: String,
                         duration: Option[String] = None,
                         replication: Option[String] = None,
                         shardDuration: Option[String] = None,
                         name: Option[String] = None): Result = await(createDatabase(dbName, duration, replication, shardDuration, name))

  def dropDatabaseSync(dbName: String): Result = await(dropDatabase(dbName))

  def dropMeasurementSync(dbName: String, measurementName: String): Result = await(dropMeasurement(dbName, measurementName))

  def dropShardSync(shardId: Int): Result = await(dropShard(shardId))

  def showMeasurementSync(dbName: String)(implicit reader: InfluxReader[MeasurementInfo]): QueryResult[MeasurementInfo] = await(showMeasurement(dbName))

  def showRetentionPoliciesSync(dbName: String)(implicit reader: InfluxReader[RetentionPolicyInfo]): QueryResult[RetentionPolicyInfo] = await(showRetentionPolicies(dbName))

  def showDatabaseSync()(implicit reader: InfluxReader[DatabaseInfo]): QueryResult[DatabaseInfo] = await(showDatabases())

  //ASYNCHRONOUS API
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
