package com.github.fsanaulla.core.api.management

import com.github.fsanaulla.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.query.RetentionPolicyManagementQuery
import com.github.fsanaulla.core.utils.DefaultInfluxImplicits._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[fsanaulla] trait RetentionPolicyManagement[M[_], R, U, E] extends RetentionPolicyManagementQuery[U] {
  self: RequestHandler[M, R, U, E]
    with ResponseHandler[M, R]
    with QueryHandler[U]
    with Mappable[M, R]
    with HasCredentials =>

  /**
    * Create retention policy for specified database
    * @param rpName        - retention policy name
    * @param dbName        - database name
    * @param duration      - retention policy duration
    * @param replication   - replication factor
    * @param shardDuration - shard duration value
    * @param default       - use default
    * @return              - execution result
    */
  final def createRetentionPolicy(
                                   rpName: String,
                                   dbName: String,
                                   duration: String,
                                   replication: Int = 1,
                                   shardDuration: Option[String] = None,
                                   default: Boolean = false): M[Result] = {

    require(replication > 0, "Replication must greater that 0")

    m.mapTo(
      readRequest(createRetentionPolicyQuery(rpName, dbName, duration, replication, shardDuration, default)),
      toResult
    )
  }

  /** Update retention policy */
  final def updateRetentionPolicy(
                                   rpName: String,
                                   dbName: String,
                                   duration: Option[String] = None,
                                   replication: Option[Int] = None,
                                   shardDuration: Option[String] = None,
                                   default: Boolean = false): M[Result] =
    m.mapTo(
      readRequest(updateRetentionPolicyQuery(rpName, dbName, duration, replication, shardDuration, default)),
      toResult
    )


  /** Drop retention policy */
  final def dropRetentionPolicy(rpName: String, dbName: String): M[Result] =
    m.mapTo(readRequest(dropRetentionPolicyQuery(rpName, dbName)), toResult)

  /** Show list of retention polices */
  final def showRetentionPolicies(dbName: String): M[QueryResult[RetentionPolicyInfo]] =
    m.mapTo(readRequest(showRetentionPoliciesQuery(dbName)), toQueryResult[RetentionPolicyInfo])

}
