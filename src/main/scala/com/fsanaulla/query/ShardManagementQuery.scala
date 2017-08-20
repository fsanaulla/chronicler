package com.fsanaulla.query

import akka.http.scaladsl.model.Uri
import com.fsanaulla.model.InfluxCredentials
import com.fsanaulla.utils.QueryBuilder

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait ShardManagementQuery extends QueryBuilder {

  protected def dropShardQuery(shardId: Int)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"DROP SHARD $shardId"))
  }

  protected def showShards()(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams("SHOW SHARDS"))
  }

  protected def showShardGroups()(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams("SHOW SHARD GROUPS"))
  }

}
