/*
 * Copyright 2017-2019 Faiaz Sanaulla
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

package com.github.fsanaulla.chronicler.core.management

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, ResponseCode}
import com.github.fsanaulla.chronicler.core.components._
import com.github.fsanaulla.chronicler.core.implicits._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.ShardManagementQuery

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[chronicler] trait ShardManagement[F[_], Resp, Uri, Entity] extends ShardManagementQuery[Uri] {
  implicit val qb: QueryBuilder[Uri]
  implicit val re: RequestExecutor[F, Resp, Uri, Entity]
  implicit val rh: ResponseHandler[Resp]
  implicit val F: Functor[F]

  /** Drop shard */
  final def dropShard(shardId: Int): F[ErrorOr[ResponseCode]] =
    F.map(re.executeUri(dropShardQuery(shardId)))(rh.writeResult)

  /** Show shard groups */
  final def showShardGroups: F[ErrorOr[Array[ShardGroupsInfo]]] =
    F.map(re.executeUri(showShardGroupsQuery))(rh.toShardGroupQueryResult)

  /** Show shards */
  final def showShards: F[ErrorOr[Array[ShardInfo]]] =
    F.map(re.executeUri(showShardsQuery))(rh.toShardQueryResult)
}
