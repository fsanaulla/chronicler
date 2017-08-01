package com.fsanaulla.api

import com.fsanaulla.InfluxClient
import com.fsanaulla.model.{CreateResult, DeleteResult, UpdateResult}
import com.fsanaulla.query.RetentionPolicyManagementQuery

import scala.concurrent.Future

trait RetentionPolicyManagement extends RetentionPolicyManagementQuery { self: InfluxClient =>

  def createRetentionPolicy(rpName: String,
                            dbName: String,
                            duration: String,
                            replication: Int = 1,
                            shardDuration: Option[String] = None,
                            default: Boolean = false): Future[CreateResult] = {
    require(replication > 0, "Replication must greater that 0")

    buildRequest(createRetentionPolicyQuery(rpName, dbName, duration, replication, shardDuration, default))
      .flatMap(resp => toResponse(resp, CreateResult(200, isSuccess = true)))
  }

  def updateRetentionPolicy(rpName: String,
                            dbName: String,
                            duration: Option[String] = None,
                            replication: Option[Int] = None,
                            shardDuration: Option[String] = None,
                            default: Boolean = false): Future[UpdateResult] = {
    buildRequest(updateRetentionPolicyQuery(rpName, dbName, duration, replication, shardDuration, default))
      .flatMap(resp => toResponse(resp, UpdateResult(200, isSuccess = true)))
  }

  def dropRetentionPolicy(rpName: String, dbName: String): Future[DeleteResult] = {
    buildRequest(dropRetentionPolicyQuery(rpName, dbName))
      .flatMap(resp => toResponse(resp, DeleteResult(200, isSuccess = true)))
  }
}
