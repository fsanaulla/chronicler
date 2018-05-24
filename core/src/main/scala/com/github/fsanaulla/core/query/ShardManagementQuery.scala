package com.github.fsanaulla.core.query

import com.github.fsanaulla.core.handlers.QueryHandler
import com.github.fsanaulla.core.model.HasCredentials

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait ShardManagementQuery[U] {
  self: QueryHandler[U] with HasCredentials =>

  def dropShardQuery(shardId: Int): U = {
    buildQuery("/query", buildQueryParams(s"DROP SHARD $shardId"))
  }

  def showShardsQuery(): U = {
    buildQuery("/query", buildQueryParams("SHOW SHARDS"))
  }

  def showShardGroupsQuery(): U = {
    buildQuery("/query", buildQueryParams("SHOW SHARD GROUPS"))
  }

}
