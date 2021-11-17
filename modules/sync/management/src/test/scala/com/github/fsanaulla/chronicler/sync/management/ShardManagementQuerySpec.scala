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

package com.github.fsanaulla.chronicler.sync.management

import com.github.fsanaulla.chronicler.core.query.ShardManagementQuery
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.model.Uri
import com.github.fsanaulla.chronicler.sync.shared.SyncQueryBuilder
import com.github.fsanaulla.chronicler.sync.shared.SyncQueryBuilder

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
class ShardManagementQuerySpec extends AnyFlatSpec with Matchers with ShardManagementQuery[Uri] {

  implicit val qb = new SyncQueryBuilder("localhost", 8086)

  it should "drop shard by id" in {
    dropShardQuery(5).toString shouldEqual queryTester("DROP SHARD 5")
  }

  it should "show shards" in {
    showShardsQuery.toString shouldEqual queryTester("SHOW SHARDS")
  }

  it should "show shards without auth" in {
    showShardsQuery.toString shouldEqual queryTester("SHOW SHARDS")
  }

  it should "show shard groups" in {
    showShardGroupsQuery.toString shouldEqual queryTester("SHOW SHARD GROUPS")
  }
}
