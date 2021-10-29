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

package com.github.fsanaulla.chronicler.core.query

import com.github.fsanaulla.chronicler.core.components.QueryBuilder

/** Created by Author: fayaz.sanaulla@gmail.com Date: 19.08.17
  */
private[chronicler] trait ShardManagementQuery[U] {

  private[chronicler] final def dropShardQuery(shardId: Int)(implicit qb: QueryBuilder[U]): U =
    qb.buildQuery("/query", qb.appendCredentials(s"DROP SHARD $shardId"))

  private[chronicler] final def showShardsQuery(implicit qb: QueryBuilder[U]): U =
    qb.buildQuery("/query", qb.appendCredentials("SHOW SHARDS"))

  private[chronicler] final def showShardGroupsQuery(implicit qb: QueryBuilder[U]): U =
    qb.buildQuery("/query", qb.appendCredentials("SHOW SHARD GROUPS"))

}
