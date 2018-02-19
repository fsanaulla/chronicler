package com.github.fsanaulla.integration

import com.github.fsanaulla.core.model.ShardGroupsInfo
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.utils.TestHelper._
import com.github.fsanaulla.{InfluxAsyncHttpClient, InfluxClientFactory}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class ShardManagementSpec extends TestSpec {

  val testDb = "_internal"
  lazy val influx: InfluxAsyncHttpClient = InfluxClientFactory.createHttpClient(host = influxHost, username = credentials.username, password = credentials.password)

  "shard operations" should "get/drop shards" in {

    val shards = influx.getShards(testDb).futureValue.queryResult

    shards should not be Nil

    influx.dropShard(shards.head.id).futureValue shouldEqual OkResult

    influx.getShards(testDb).futureValue.queryResult should not be shards
  }

  it should "get shards gruops" in {

    val shardGroups = influx.showShardGroups().futureValue.queryResult

    shardGroups should not equal Nil

    shardGroups shouldBe a[Seq[_]]

    shardGroups.head shouldBe a[ShardGroupsInfo]
  }

  influx.close()
}
