package com.github.fsanaulla.chronicler.core.api.management

import com.github.fsanaulla.chronicler.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.ShardManagementQuery
import com.github.fsanaulla.chronicler.core.utils.DefaultInfluxImplicits._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[chronicler] trait ShardManagement[M[_], R, U, E] extends ShardManagementQuery[U] {
  self: RequestHandler[M, R, U, E]
    with ResponseHandler[M, R]
    with QueryHandler[U]
    with Mappable[M, R]
    with HasCredentials =>

  /** Drop shard */
  final def dropShard(shardId: Int): M[WriteResult] =
    mapTo(readRequest(dropShardQuery(shardId)), toResult)

  /** Show shard groups */
  final def showShardGroups: M[QueryResult[ShardGroupsInfo]] =
    mapTo(readRequest(showShardGroupsQuery()), toShardGroupQueryResult)

  /** Show shards */
  final def showShards: M[QueryResult[ShardInfo]] =
    mapTo(readRequest(showShardsQuery()), toShardQueryResult)

//  def getShards(dbName: String): Future[QueryResult[Shard]] = {
//    showShards().map { qr =>
//      val seq = qr.queryResult.find(_.dbName == dbName).map(_.shards).getOrElse(Array.empty[Shard])
//
//      QueryResult[Shard](qr.code, qr.isSuccess, seq, qr.ex)
//    }
//  }
}
