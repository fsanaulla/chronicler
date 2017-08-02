package com.fsanaulla.api

import com.fsanaulla.InfluxClient
import com.fsanaulla.model._
import com.fsanaulla.query.DataManagementQuery

import scala.concurrent.Future

trait DataManagement extends DataManagementQuery { self: InfluxClient =>

  def createDatabase(dbName: String,
                     duration: Option[String] = None,
                     replication: Option[String] = None,
                     shardDuration: Option[String] = None,
                     name: Option[String] = None): Future[CreateResult] = {
    buildRequest(createDatabaseQuery(dbName, duration, replication, shardDuration, name))
      .flatMap(resp => toResult(resp, CreateResult(200, isSuccess = true)))
  }

  def dropDatabase(dbName: String): Future[DeleteResult] = {
    buildRequest(dropDatabaseQuery(dbName))
      .flatMap(resp => toResult(resp, DeleteResult(200, isSuccess = true)))
  }

  def dropMeasurement(dbName: String, measurementName: String): Future[DeleteResult] = {
    buildRequest(dropMeasurementQuery(dbName, measurementName))
      .flatMap(resp => toResult(resp, DeleteResult(200, isSuccess = true)))
  }

  def dropShard(shardId: Int): Future[DeleteResult] = {
    buildRequest(dropShardQuery(shardId))
      .flatMap(resp => toResult(resp, DeleteResult(200, isSuccess = true)))
  }

  def showMeasurement(dbName: String)(implicit reader: InfluxReader[MeasurementInfo]): Future[Seq[MeasurementInfo]] = {
    buildRequest(showMeasurementQuery(dbName))
      .flatMap(toQueryJsResult)
      .map(_.map(reader.read))
  }

  def showRetentionPolicies(dbName: String)(implicit reader: InfluxReader[RetentionPolicyInfo]): Future[Seq[RetentionPolicyInfo]] = {
    buildRequest(showRetentionPoliciesQuery(dbName))
      .flatMap(toQueryJsResult)
      .map(_.map(reader.read))
  }

  def showDatabases()(implicit reader: InfluxReader[DatabaseInfo]): Future[Seq[DatabaseInfo]] = {
    buildRequest(showDatabasesQuery())
      .flatMap(toQueryJsResult)
      .map(_.map(reader.read).filterNot(_.dbName == "_internal"))
  }
}
