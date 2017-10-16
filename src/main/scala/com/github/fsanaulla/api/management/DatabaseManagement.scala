package com.github.fsanaulla.api.management

import com.github.fsanaulla.clients.InfluxHttpClient
import com.github.fsanaulla.model.InfluxImplicits._
import com.github.fsanaulla.model.{FieldInfo, QueryResult, Result, TagValue}
import com.github.fsanaulla.query.DataManagementQuery
import com.github.fsanaulla.utils.ResponseHandler._

import scala.concurrent.Future

private[fsanaulla] trait DatabaseManagement extends DataManagementQuery { self: InfluxHttpClient =>

  def createDatabase(dbName: String, duration: Option[String] = None, replication: Option[Int] = None, shardDuration: Option[String] = None, rpName: Option[String] = None): Future[Result] = {
    buildRequest(createDatabaseQuery(dbName, duration, replication, shardDuration, rpName))
      .flatMap(toResult)
  }

  def dropDatabase(dbName: String): Future[Result] = {
    buildRequest(dropDatabaseQuery(dbName)).flatMap(toResult)
  }

  def dropMeasurement(dbName: String, measurementName: String): Future[Result] = {
    buildRequest(dropMeasurementQuery(dbName, measurementName)).flatMap(toResult)
  }

  def showMeasurement(dbName: String): Future[QueryResult[String]] = {
    buildRequest(showMeasurementQuery(dbName)).flatMap(toQueryResult[String])
  }

  def showDatabases(): Future[QueryResult[String]] = {
    buildRequest(showDatabasesQuery())
      .flatMap(toQueryResult[String])
      .map(res => res.copy(queryResult = res.queryResult))
  }

  def showFieldKeys(dbName: String, measurementName: String): Future[QueryResult[FieldInfo]] = {
    buildRequest(showFieldKeysQuery(dbName, measurementName)).flatMap(toQueryResult[FieldInfo])
  }

  def showTagKeys(dbName: String, measurementName: String, whereClause: Option[String] = None, limit: Option[Int] = None, offset: Option[Int] = None): Future[QueryResult[String]] = {
    buildRequest(showTagKeysQuery(dbName, measurementName, whereClause, limit, offset))
      .flatMap(toQueryResult[String])
  }

  def showTagValues(dbName: String,
                    measurementName: String,
                    withKey: Seq[String],
                    whereClause: Option[String] = None,
                    limit: Option[Int] = None,
                    offset: Option[Int] = None): Future[QueryResult[TagValue]] = {
    buildRequest(showTagValuesQuery(dbName, measurementName, withKey, whereClause, limit, offset))
      .flatMap(toQueryResult[TagValue])
  }
}
