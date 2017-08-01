package com.fsanaulla.api

import com.fsanaulla.InfluxClient
import com.fsanaulla.model._
import com.fsanaulla.query.DataManagementQuery
import com.fsanaulla.utils.DataManagementHelper

import scala.concurrent.Future

trait DataManagement extends DataManagementQuery with DataManagementHelper { self: InfluxClient =>

  def createDatabase(dbName: String,
                     duration: Option[String] = None,
                     replication: Option[String] = None,
                     shardDuration: Option[String] = None,
                     name: Option[String] = None): Future[CreateResult] = {
    buildRequest(createDatabaseQuery(dbName, duration, replication, shardDuration, name))
      .flatMap(resp => toResponse(resp, CreateResult(200, isSuccess = true)))
  }

  def dropDatabase(dbName: String): Future[DeleteResult] = {
    buildRequest(dropDatabaseQuery(dbName))
      .flatMap(resp => toResponse(resp, DeleteResult(200, isSuccess = true)))
  }

  def dropMeasurement(dbName: String, measurementName: String): Future[DeleteResult] = {
    buildRequest(dropMeasurementQuery(dbName, measurementName))
      .flatMap(resp => toResponse(resp, DeleteResult(200, isSuccess = true)))
  }

  def dropShard(shardId: Int): Future[DeleteResult] = {
    buildRequest(dropShardQuery(shardId))
      .flatMap(resp => toResponse(resp, DeleteResult(200, isSuccess = true)))
  }

  def showMeasurement(dbName: String): Future[Seq[MeasurementInfo]] = {
    buildRequest(showMeasurementQuery(dbName))
      .flatMap(resp => toQueryResponse(resp, toMeasurementInfo(resp)))
  }

  def showRetentionPolicies(dbName: String): Future[Seq[RetentionPolicyInfo]] = {
    buildRequest(showRetentionPoliciesQuery(dbName))
      .flatMap(resp => toQueryResponse(resp, toRetentionPolicy(resp)))
  }

  def showDatabases(): Future[Seq[DatabaseInfo]] = {
    buildRequest(showDatabasesQuery())
      .flatMap(resp => toQueryResponse(resp, toDatabaseInfo(resp)))
  }
}
