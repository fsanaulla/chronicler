package com.github.fsanaulla.integration

import com.github.fsanaulla.InfluxClientFactory
import com.github.fsanaulla.core.model.ShardGroupsInfo
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.utils.TestHelper._

import scala.concurrent.ExecutionContext.Implicits.global

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
