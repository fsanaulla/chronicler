package com.fsanaulla.api

import com.fsanaulla.InfluxClient
import com.fsanaulla.model.InfluxImplicits._
import com.fsanaulla.model.{QueryResult, Result, ShardGroupsInfo, ShardInfo}
import com.fsanaulla.query.ShardManagementQuery
import com.fsanaulla.utils.ResponseHandler._

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait ShardManagement extends ShardManagementQuery { self: InfluxClient =>

  def dropShard(shardId: Int): Future[Result] = {
    buildRequest(dropShardQuery(shardId)).flatMap(toResult)
  }

  def showShardGroups(): Future[QueryResult[ShardGroupsInfo]] = {
    buildRequest(showShardGroups()).flatMap(toShardGroupQueryResult)
  }

  def showShards(): Future[QueryResult[ShardInfo]] = {
    buildRequest(showShards()).flatMap(toShardQueryResult)
  }
}
