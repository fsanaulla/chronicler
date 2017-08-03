package com.fsanaulla.api

import com.fsanaulla.InfluxClient
import com.fsanaulla.model.Result
import com.fsanaulla.query.RetentionPolicyManagementQuery
import com.fsanaulla.utils.ResponseWrapper.toResult

import scala.concurrent.Future

trait RetentionPolicyManagement extends RetentionPolicyManagementQuery { self: InfluxClient =>

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
}
