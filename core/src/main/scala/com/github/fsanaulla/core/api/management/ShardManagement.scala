package com.github.fsanaulla.core.api.management

import com.github.fsanaulla.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.query.ShardManagementQuery
import com.github.fsanaulla.core.utils.DefaultInfluxImplicits._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait ShardManagement[M[_], R, U, E] extends ShardManagementQuery[U] {
  self: RequestHandler[M, R, U, E]
    with ResponseHandler[M, R]
    with QueryHandler[U]
    with Mappable[M, R]
    with HasCredentials =>

  /** Drop shard */
  def dropShard(shardId: Int): M[Result] =
    m.mapTo(readRequest(dropShardQuery(shardId)), toResult)

  /** Show shard groups */
  def showShardGroups: M[QueryResult[ShardGroupsInfo]] =
    m.mapTo(readRequest(showShardGroupsQuery()), toShardGroupQueryResult)

  /** Show shards */
  def showShards: M[QueryResult[ShardInfo]] =
    m.mapTo(readRequest(showShardsQuery()), toShardQueryResult)

//  def getShards(dbName: String): Future[QueryResult[Shard]] = {
//    showShards().map { qr =>
//      val seq = qr.queryResult.find(_.dbName == dbName).map(_.shards).getOrElse(Array.empty[Shard])
//
//      QueryResult[Shard](qr.code, qr.isSuccess, seq, qr.ex)
//    }
//  }
}
