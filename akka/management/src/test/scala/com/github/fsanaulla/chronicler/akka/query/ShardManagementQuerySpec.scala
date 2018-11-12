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

package com.github.fsanaulla.chronicler.akka.query

import _root_.akka.http.scaladsl.model.Uri
import com.github.fsanaulla.chronicler.akka.TestHelper._
import com.github.fsanaulla.chronicler.akka.shared.handlers.AkkaQueryBuilder
import com.github.fsanaulla.chronicler.core.model.HasCredentials
import com.github.fsanaulla.chronicler.core.query.ShardManagementQuery
import com.github.fsanaulla.chronicler.testing.unit.{EmptyCredentials, FlatSpecWithMatchers, NonEmptyCredentials}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
class ShardManagementQuerySpec extends FlatSpecWithMatchers {

  trait Env extends AkkaQueryBuilder with ShardManagementQuery[Uri] { self: HasCredentials =>
    val host = "localhost"
    val port = 8086
  }
  trait AuthEnv extends Env with NonEmptyCredentials
  trait NonAuthEnv extends Env with EmptyCredentials

  "ShardManagementQuery" should "drop shard by id" in new AuthEnv {
    dropShardQuery(5) shouldEqual queryTesterAuth("DROP SHARD 5")(credentials.get)
  }

  it should "drop shard by id without auth" in new NonAuthEnv {
    dropShardQuery(5) shouldEqual queryTester("DROP SHARD 5")
  }

  it should "show shards" in new AuthEnv {
    showShardsQuery shouldEqual queryTesterAuth("SHOW SHARDS")(credentials.get)
  }

  it should "show shards without auth" in new NonAuthEnv {
    showShardsQuery shouldEqual queryTester("SHOW SHARDS")
  }

  it should "show shard groups" in new AuthEnv {
    showShardGroupsQuery shouldEqual queryTesterAuth("SHOW SHARD GROUPS")(credentials.get)
  }

  it should "show shard groups without auth" in new NonAuthEnv {
    showShardGroupsQuery shouldEqual queryTester("SHOW SHARD GROUPS")
  }
}
