package com.github.fsanaulla.chronicler.core.api.management

import com.github.fsanaulla.chronicler.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.DataManagementQuery
import com.github.fsanaulla.chronicler.core.utils.DefaultInfluxImplicits._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[chronicler] trait DatabaseManagement[M[_], Req, Resp, Uri, Entity] extends DataManagementQuery[Uri] {
  self: RequestHandler[M, Req, Resp]
    with ResponseHandler[M, Resp]
    with QueryHandler[Uri]
    with Mappable[M, Resp]
    with RequestBuilder[Uri, Req]
    with HasCredentials =>

  /**
    * Create database
    * @param dbName        - database name
    * @param duration      - database duration
    * @param replication   - replication
    * @param shardDuration - shard duration
    * @param rpName        - retention policy name
    * @return              - execution R
    */
  final def createDatabase(dbName: String,
                           duration: Option[String] = None,
                           replication: Option[Int] = None,
                           shardDuration: Option[String] = None,
                           rpName: Option[String] = None): M[WriteResult] =
    mapTo(execute(createDatabaseQuery(dbName, duration, replication, shardDuration, rpName)), toResult)


  /** Drop database */
  final def dropDatabase(dbName: String): M[WriteResult] =
    mapTo(execute(dropDatabaseQuery(dbName)), toResult)

  /** Drop measurement */
  final def dropMeasurement(dbName: String, measurementName: String): M[WriteResult] =
    mapTo(execute(dropMeasurementQuery(dbName, measurementName)), toResult)

  /** Show measurements */
  final def showMeasurement(dbName: String): M[QueryResult[String]] =
    mapTo(execute(showMeasurementQuery(dbName)), toQueryResult[String])

  /** Show database list */
  final def showDatabases(): M[QueryResult[String]] =
    mapTo(execute(showDatabasesQuery()), toQueryResult[String])

  /** Show field tags list */
  final def showFieldKeys(dbName: String, measurementName: String): M[QueryResult[FieldInfo]] =
    mapTo(execute(showFieldKeysQuery(dbName, measurementName)), toQueryResult[FieldInfo])

  /** Show tags keys list */
  final def showTagKeys(dbName: String, 
                        measurementName: String, 
                        whereClause: Option[String] = None, 
                        limit: Option[Int] = None, 
                        offset: Option[Int] = None): M[QueryResult[String]] =
    mapTo(execute(showTagKeysQuery(dbName, measurementName, whereClause, limit, offset)), toQueryResult[String])

  /** Show tag values list */
  final def showTagValues(dbName: String, 
                          measurementName: String, 
                          withKey: Seq[String], 
                          whereClause: Option[String] = None, 
                          limit: Option[Int] = None, 
                          offset: Option[Int] = None): M[QueryResult[TagValue]] =
    mapTo(execute(showTagValuesQuery(dbName, measurementName, withKey, whereClause, limit, offset)), toQueryResult[TagValue])
}
