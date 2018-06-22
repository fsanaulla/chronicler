package com.github.fsanaulla.chronicler.async.unit

import com.github.fsanaulla.chronicler.async.TestHelper._
import com.github.fsanaulla.chronicler.async.handlers.AsyncQueryHandler
import com.github.fsanaulla.chronicler.core.query.ShardManagementQuery
import com.github.fsanaulla.chronicler.testing.unit.{EmptyCredentials, FlatSpecWithMatchers, NonEmptyCredentials}
import com.softwaremill.sttp.Uri

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
class ShardManagementQuerySpec extends FlatSpecWithMatchers {

  trait Env extends AsyncQueryHandler with ShardManagementQuery[Uri] {
    val host = "localhost"
    val port = 8086
  }
  trait AuthEnv extends Env with NonEmptyCredentials
  trait NonAuthEnv extends Env with EmptyCredentials

  "ShardManagementQuery" should "drop shard by id" in new AuthEnv {
    dropShardQuery(5).toString() shouldEqual queryTesterAuth("DROP SHARD 5")(credentials.get)
  }

  it should "drop shard by id without auth" in new NonAuthEnv {
    dropShardQuery(5).toString() shouldEqual queryTester("DROP SHARD 5")
  }

  it should "show shards" in new AuthEnv {
    showShardsQuery().toString() shouldEqual queryTesterAuth("SHOW SHARDS")(credentials.get)
  }

  it should "show shards without auth" in new NonAuthEnv {
    showShardsQuery().toString() shouldEqual queryTester("SHOW SHARDS")
  }

  it should "show shard groups" in new AuthEnv {
    showShardGroupsQuery().toString() shouldEqual queryTesterAuth("SHOW SHARD GROUPS")(credentials.get)
  }

  it should "show shard groups without auth" in new NonAuthEnv {
    showShardGroupsQuery().toString() shouldEqual queryTester("SHOW SHARD GROUPS")
  }
}
