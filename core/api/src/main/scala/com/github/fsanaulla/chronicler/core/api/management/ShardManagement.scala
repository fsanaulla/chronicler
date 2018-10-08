/*
 * Copyright 2017-2018 Faiaz Sanaulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fsanaulla.chronicler.core.api.management

import com.github.fsanaulla.chronicler.core.handlers.ResponseHandler
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.ShardManagementQuery
import com.github.fsanaulla.chronicler.core.typeclasses.{FlatMap, QueryBuilder, RequestExecutor}
import com.github.fsanaulla.chronicler.core.utils.DefaultInfluxImplicits._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[chronicler] trait ShardManagement[F[_], Req, Resp, Uri, Entity] extends ShardManagementQuery[Uri] {
  self: RequestExecutor[F, Req, Resp, Uri]
    with ResponseHandler[F, Resp]
    with QueryBuilder[Uri]
    with FlatMap[F]
    with HasCredentials =>

  /** Drop shard */
  final def dropShard(shardId: Int): F[WriteResult] =
    flatMap(execute(dropShardQuery(shardId)))(toResult)

  /** Show shard groups */
  final def showShardGroups: F[QueryResult[ShardGroupsInfo]] =
    flatMap(execute(showShardGroupsQuery))(toShardGroupQueryResult)

  /** Show shards */
  final def showShards: F[QueryResult[ShardInfo]] =
    flatMap(execute(showShardsQuery))(toShardQueryResult)
}
