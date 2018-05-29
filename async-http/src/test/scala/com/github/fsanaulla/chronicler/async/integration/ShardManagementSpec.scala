package com.github.fsanaulla.chronicler.async.integration

import com.github.fsanaulla.chronicler.async.{Influx, InfluxAsyncHttpClient}
import com.github.fsanaulla.chronicler.testing.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.{DockerizedInfluxDB, TestSpec}
import com.github.fsanaulla.core.model.{ShardGroupsInfo, ShardInfo}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class ShardManagementSpec extends TestSpec with DockerizedInfluxDB {

  val testDb = "_internal"

  lazy val influx: InfluxAsyncHttpClient =
    Influx.connect(host, port, Some(creds))

  "shard operations" should "show shards" in {

    influx.createDatabase(testDb, shardDuration = Some("1s")).futureValue shouldEqual OkResult

    val shards = influx.showShards.futureValue.queryResult

    shards should not be Array.empty[ShardInfo]
  }

  it should "show shards groupe" in {

    val shardGroups = influx.showShardGroups.futureValue.queryResult

    shardGroups should not equal Array.empty[ShardGroupsInfo]

    shardGroups shouldBe a [Array[_]]

    shardGroups.head shouldBe a [ShardGroupsInfo]

    influx.close() shouldEqual {}
  }
}
