package com.github.fsanaulla.core.api.management

import com.github.fsanaulla.core.handlers.RequestHandler
import com.github.fsanaulla.core.handlers.query.QueryHandler
import com.github.fsanaulla.core.handlers.response.ResponseHandler
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.query.ShardManagementQuery
import com.github.fsanaulla.core.utils.DefaultInfluxImplicits._

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait ShardManagement[R, U, M, E] extends ShardManagementQuery[U] {
  self: RequestHandler[R, U, M, E]
    with ResponseHandler[R]
    with QueryHandler[U]
    with HasCredentials
    with Executable =>

  def dropShard(shardId: Int): Future[Result] = {
    readRequest(dropShardQuery(shardId)).flatMap(toResult)
  }

  def showShardGroups(): Future[QueryResult[ShardGroupsInfo]] = {
    readRequest(showShardGroupsQuery()).flatMap(toShardGroupQueryResult)
  }

  def showShards(): Future[QueryResult[ShardInfo]] = {
    readRequest(showShardsQuery()).flatMap(toShardQueryResult)
  }

  def getShards(dbName: String): Future[QueryResult[Shard]] = {
    showShards().map { qr =>
      val seq = qr.queryResult.find(_.dbName == dbName).map(_.shards).getOrElse(Array.empty[Shard])

      QueryResult[Shard](qr.code, qr.isSuccess, seq, qr.ex)
    }
  }
}
