package com.github.fsanaulla.chronicler.urlhttp.integration

import com.github.fsanaulla.chronicler.testing.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.{DockerizedInfluxDB, TestSpec}
import com.github.fsanaulla.chronicler.urlhttp.{Influx, InfluxUrlHttpClient}
import com.github.fsanaulla.core.model.{ShardGroupsInfo, ShardInfo}
import org.scalatest.TryValues

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class ShardManagementSpec extends TestSpec with DockerizedInfluxDB with TryValues {

  val testDb = "_internal"

  lazy val influx: InfluxUrlHttpClient =
    Influx.connect(host, port, Some(creds))

  "shard operations" should "show shards" in {

    influx.createDatabase(testDb, shardDuration = Some("1s")).success.value shouldEqual OkResult

    val shards = influx.showShards.success.value.queryResult

    shards should not be Array.empty[ShardInfo]
  }

  it should "show shards groupe" in {

    val shardGroups = influx.showShardGroups.success.value.queryResult

    shardGroups should not equal Array.empty[ShardGroupsInfo]

    shardGroups shouldBe a [Array[_]]

    shardGroups.head shouldBe a [ShardGroupsInfo]

    influx.close() shouldEqual {}
  }
}
