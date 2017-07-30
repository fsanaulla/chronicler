package com.fsanaulla.api

import akka.http.scaladsl.model.HttpResponse
import com.fsanaulla.InfluxClient
import com.fsanaulla.query.RetentionPolicyManagementQuery

import scala.concurrent.Future

trait RetentionPolicyManagement
  extends RetentionPolicyManagementQuery
    with RequestBuilder { self: InfluxClient =>

  def createRetentionPolicy(rpName: String,
                            dbName: String,
                            duration: String,
                            replication: Int = 1,
                            shardDuration: Option[String] = None,
                            default: Boolean = false): Future[HttpResponse] = {
    require(replication > 0, "Replication must greater that 0")
    buildRequest(createRetentionPolicyQuery(rpName, dbName, duration, replication, shardDuration, default))
  }

  def updateRetentionPolicy(rpName: String,
                            dbName: String,
                            duration: Option[String] = None,
                            replication: Option[Int] = None,
                            shardDuration: Option[String] = None,
                            default: Boolean = false): Future[HttpResponse] = {
    buildRequest(updateRetentionPolicyQuery(rpName, dbName, duration, replication, shardDuration, default))
  }

  def dropRetentionPolicy(rpName: String, dbName: String): Future[HttpResponse] = {
    buildRequest(dropRetentionPolicyQuery(rpName, dbName))
  }
}
