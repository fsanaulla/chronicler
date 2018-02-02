package com.github.fsanaulla.api.management

import com.github.fsanaulla.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.model.InfluxImplicits._
import com.github.fsanaulla.model._
import com.github.fsanaulla.query.ShardManagementQuery

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
    readRequest(dropShardQuery(shardId)).flatMap(toResult)
  }

  def showShardGroups(): Future[QueryResult[ShardGroupsInfo]] = {
    readRequest(showShardGroups()).flatMap(toShardGroupQueryResult)
  }

  def showShards(): Future[QueryResult[ShardInfo]] = {
    readRequest(showShards()).flatMap(toShardQueryResult)
  }

  def getShards(dbName: String): Future[QueryResult[Shard]] = {
    showShards().map { queryResult =>
      val seq = queryResult.queryResult.find(_.dbName == dbName).map(_.shards).getOrElse(Nil)

      QueryResult[Shard](queryResult.code, queryResult.isSuccess, seq, queryResult.ex)
    }
  }
}
