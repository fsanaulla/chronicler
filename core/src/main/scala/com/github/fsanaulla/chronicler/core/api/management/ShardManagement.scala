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
private[chronicler] trait ShardManagement[M[_], Req, Resp, Uri, Entity] extends ShardManagementQuery[Uri] {
  self: RequestHandler[M, Req, Resp, Uri]
    with ResponseHandler[M, Resp]
    with QueryHandler[Uri]
    with Mappable[M, Resp]
    with ImplicitRequestBuilder[Uri, Req]
    with HasCredentials =>

  /** Drop shard */
  final def dropShard(shardId: Int): M[WriteResult] =
    mapTo(execute(dropShardQuery(shardId)), toResult)

  /** Show shard groups */
  final def showShardGroups: M[QueryResult[ShardGroupsInfo]] =
    mapTo(execute(showShardGroupsQuery()), toShardGroupQueryResult)

  /** Show shards */
  final def showShards: M[QueryResult[ShardInfo]] =
    mapTo(execute(showShardsQuery()), toShardQueryResult)
}
