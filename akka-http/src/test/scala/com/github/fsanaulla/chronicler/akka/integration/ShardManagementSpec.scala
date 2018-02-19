package com.github.fsanaulla.chronicler.akka.integration

import com.github.fsanaulla.chronicler.akka.InfluxClientFactory
import com.github.fsanaulla.chronicler.akka.utils.TestHelper._
import com.github.fsanaulla.core.model.ShardGroupsInfo
import com.github.fsanaulla.core.test.utils.TestSpec

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class ShardManagementSpec extends TestSpec {

  val testDb = "_internal"

  "shard operations" should "correctly work" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClientFactory.createHttpClient(host = influxHost, username = credentials.username, password = credentials.password)

    val shards = influx.getShards(testDb).futureValue.queryResult

    shards should not be Nil

    influx.dropShard(shards.head.id).futureValue shouldEqual OkResult

    influx.getShards(testDb).futureValue.queryResult should not be shards

    val shardGroups = influx.showShardGroups().futureValue.queryResult

    shardGroups should not equal Nil

    shardGroups shouldBe a [Seq[_]]

    shardGroups.head shouldBe a [ShardGroupsInfo]

    influx.close()
  }
}
