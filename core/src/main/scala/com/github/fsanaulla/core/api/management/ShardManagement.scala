package com.github.fsanaulla.core.api.management

import com.github.fsanaulla.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.core.model.InfluxImplicits._
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.query.ShardManagementQuery

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait ShardManagement[R, U, M, E] extends ShardManagementQuery[U] {
  self: RequestHandler[R, U, M, E]
    with ResponseHandler[R]
    with QueryHandler[U]
    with HasCredentials =>

  protected implicit val ex: ExecutionContext

  def dropShard(shardId: Int): Future[Result] = {
    buildRequest(dropShardQuery(shardId)).flatMap(toResult)
  }

  def showShardGroups(): Future[QueryResult[ShardGroupsInfo]] = {
    buildRequest(showShardGroups()).flatMap(toShardGroupQueryResult)
  }

  def showShards(): Future[QueryResult[ShardInfo]] = {
    buildRequest(showShards()).flatMap(toShardQueryResult)
  }

  def getShards(dbName: String): Future[QueryResult[Shard]] = {
    showShards().map { queryResult =>
      val seq = queryResult.queryResult.find(_.dbName == dbName).map(_.shards).getOrElse(Nil)

      QueryResult[Shard](queryResult.code, queryResult.isSuccess, seq, queryResult.ex)
    }
  }
}
