package com.github.fsanaulla.core.api.management

import com.github.fsanaulla.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.core.model.InfluxImplicits._
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.query.RetentionPolicyManagementQuery

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[fsanaulla] trait RetentionPolicyManagement[R, U, M, E] extends RetentionPolicyManagementQuery[U] {
  self: RequestHandler[R, U, M, E]
    with ResponseHandler[R]
    with QueryHandler[U]
    with HasCredentials
    with Executable =>

  def createRetentionPolicy(rpName: String,
                            dbName: String,
                            duration: String,
                            replication: Int = 1,
                            shardDuration: Option[String] = None,
                            default: Boolean = false): Future[Result] = {

    require(replication > 0, "Replication must greater that 0")

    readRequest(createRetentionPolicyQuery(rpName, dbName, duration, replication, shardDuration, default))
      .flatMap(toResult)
  }

  def updateRetentionPolicy(rpName: String,
                            dbName: String,
                            duration: Option[String] = None,
                            replication: Option[Int] = None,
                            shardDuration: Option[String] = None,
                            default: Boolean = false): Future[Result] = {
    readRequest(updateRetentionPolicyQuery(rpName, dbName, duration, replication, shardDuration, default))
      .flatMap(toResult)
  }

  def dropRetentionPolicy(rpName: String, dbName: String): Future[Result] = {
    readRequest(dropRetentionPolicyQuery(rpName, dbName)).flatMap(toResult)
  }

  def showRetentionPolicies(dbName: String): Future[QueryResult[RetentionPolicyInfo]] = {
    readRequest(showRetentionPoliciesQuery(dbName)).flatMap(toQueryResult[RetentionPolicyInfo])
  }
}
