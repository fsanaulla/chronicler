package com.github.fsanaulla.chronicler.ahc.management

import com.github.fsanaulla.chronicler.core.model.{ShardGroupsInfo, ShardInfo}
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class ShardManagementSpec extends FlatSpecWithMatchers with DockerizedInfluxDB with Futures {

  val testDb = "_internal"

  lazy val influx: AhcManagementClient =
    InfluxMng(host, port, Some(creds))

  it should "show shards" in {

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
