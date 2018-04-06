package com.github.fsanaulla.core.api.management

import com.github.fsanaulla.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.query.DataManagementQuery
import com.github.fsanaulla.core.utils.DefaultInfluxImplicits._

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[fsanaulla] trait DatabaseManagement[R, U, M, E] extends DataManagementQuery[U] {
  self: RequestHandler[R, U, M, E]
    with ResponseHandler[R]
    with QueryHandler[U]
    with HasCredentials
    with Executable =>


  def createDatabase(dbName: String,
                     duration: Option[String] = None,
                     replication: Option[Int] = None,
                     shardDuration: Option[String] = None,
                     rpName: Option[String] = None): Future[Result] = {
    readRequest(createDatabaseQuery(dbName, duration, replication, shardDuration, rpName))
      .flatMap(toResult)
  }

  def dropDatabase(dbName: String): Future[Result] = {
    readRequest(dropDatabaseQuery(dbName)).flatMap(toResult)
  }

  def dropMeasurement(dbName: String, measurementName: String): Future[Result] = {
    readRequest(dropMeasurementQuery(dbName, measurementName)).flatMap(toResult)
  }

  def showMeasurement(dbName: String): Future[QueryResult[String]] = {
    readRequest(showMeasurementQuery(dbName)).flatMap(toQueryResult[String])
  }

  def showDatabases(): Future[QueryResult[String]] = {
    readRequest(showDatabasesQuery())
      .flatMap(toQueryResult[String])
      .map(res => res.copy(queryResult = res.queryResult))
  }

  def showFieldKeys(dbName: String, measurementName: String): Future[QueryResult[FieldInfo]] = {
    readRequest(showFieldKeysQuery(dbName, measurementName)).flatMap(toQueryResult[FieldInfo])
  }

  def showTagKeys(dbName: String,
                  measurementName: String,
                  whereClause: Option[String] = None,
                  limit: Option[Int] = None,
                  offset: Option[Int] = None): Future[QueryResult[String]] = {
    readRequest(showTagKeysQuery(dbName, measurementName, whereClause, limit, offset))
      .flatMap(toQueryResult[String])
  }

  def showTagValues(dbName: String,
                    measurementName: String,
                    withKey: Seq[String],
                    whereClause: Option[String] = None,
                    limit: Option[Int] = None,
                    offset: Option[Int] = None): Future[QueryResult[TagValue]] = {
    readRequest(showTagValuesQuery(dbName, measurementName, withKey, whereClause, limit, offset))
      .flatMap(toQueryResult[TagValue])
  }
}
