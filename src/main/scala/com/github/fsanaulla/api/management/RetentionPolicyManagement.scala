package com.github.fsanaulla.api.management

import com.github.fsanaulla.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.model.InfluxImplicits._
import com.github.fsanaulla.model.{HasCredentials, QueryResult, Result, RetentionPolicyInfo}
import com.github.fsanaulla.query.RetentionPolicyManagementQuery

import scala.concurrent.{ExecutionContext, Future}

private[fsanaulla] trait RetentionPolicyManagement[R, U, M, E] extends RetentionPolicyManagementQuery[U] {
  self: RequestHandler[R, U, M, E] with ResponseHandler[R] with QueryHandler[U] with HasCredentials =>


  implicit val ex: ExecutionContext


  def createRetentionPolicy(rpName: String,
                            dbName: String,
                            duration: String,
                            replication: Int = 1,
                            shardDuration: Option[String] = None,
                            default: Boolean = false): Future[Result] = {

    require(replication > 0, "Replication must greater that 0")

    buildRequest(createRetentionPolicyQuery(rpName, dbName, duration, replication, shardDuration, default))
      .flatMap(toResult)
  }

  def updateRetentionPolicy(rpName: String,
                            dbName: String,
                            duration: Option[String] = None,
                            replication: Option[Int] = None,
                            shardDuration: Option[String] = None,
                            default: Boolean = false): Future[Result] = {
    buildRequest(updateRetentionPolicyQuery(rpName, dbName, duration, replication, shardDuration, default))
      .flatMap(toResult)
  }

  def dropRetentionPolicy(rpName: String, dbName: String): Future[Result] = {
    buildRequest(dropRetentionPolicyQuery(rpName, dbName)).flatMap(toResult)
  }

  def showRetentionPolicies(dbName: String): Future[QueryResult[RetentionPolicyInfo]] = {
    buildRequest(showRetentionPoliciesQuery(dbName)).flatMap(toQueryResult[RetentionPolicyInfo])
  }
}
