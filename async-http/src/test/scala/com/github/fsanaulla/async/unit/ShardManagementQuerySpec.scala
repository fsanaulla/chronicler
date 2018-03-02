package com.github.fsanaulla.async.unit

import com.github.fsanaulla.async.utils.TestHelper._
import com.github.fsanaulla.chronicler.async.handlers.AsyncQueryHandler
import com.github.fsanaulla.core.query.ShardManagementQuery
import com.github.fsanaulla.core.test.utils.{BothCredentials, TestSpec}
import com.softwaremill.sttp.Uri

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
class ShardManagementQuerySpec
  extends TestSpec
    with AsyncQueryHandler
    with ShardManagementQuery[Uri]
    with BothCredentials {

  val host = "localhost"
  val port = 8086

  "drop shard by id" should "correctly work" in {
    dropShardQuery(5) shouldEqual queryTesterAuth("DROP SHARD 5")

    dropShardQuery(5)(emptyCredentials) shouldEqual queryTester("DROP SHARD 5")
  }

  "show shards" should "correctly work" in {
    showShards() shouldEqual queryTesterAuth("SHOW SHARDS")

    showShards()(emptyCredentials) shouldEqual queryTester("SHOW SHARDS")
  }

  "show shard groups" should "correctly work" in {
    showShardGroups() shouldEqual queryTesterAuth("SHOW SHARD GROUPS")

    showShardGroups()(emptyCredentials) shouldEqual queryTester("SHOW SHARD GROUPS")
  }
}
