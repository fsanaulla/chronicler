package com.github.fsanaulla.core.query

import com.github.fsanaulla.core.handlers.QueryHandler
import com.github.fsanaulla.core.model.InfluxCredentials

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait ShardManagementQuery[U] {
  self: QueryHandler[U] =>

  protected def dropShardQuery(shardId: Int)(implicit credentials: InfluxCredentials): U = {
    buildQuery("/query", buildQueryParams(s"DROP SHARD $shardId"))
  }

  protected def showShards()(implicit credentials: InfluxCredentials): U = {
    buildQuery("/query", buildQueryParams("SHOW SHARDS"))
  }

  protected def showShardGroups()(implicit credentials: InfluxCredentials): U = {
    buildQuery("/query", buildQueryParams("SHOW SHARD GROUPS"))
  }

}
