package com.github.fsanaulla.chronicler.ahc.management

import com.github.fsanaulla.chronicler.core.model.ShardGroupsInfo
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class ShardManagementSpec extends FlatSpec with Matchers with Futures with DockerizedInfluxDB {

  val testDb = "_internal"

  lazy val influx: AhcManagementClient =
    InfluxMng(host, port, Some(creds))

  "Shard Management API" should "show shards" in {

    influx.createDatabase(testDb, shardDuration = Some("1s")).futureValue.right.get shouldEqual 200

    val shards = influx.showShards.futureValue.right.get

    shards should not be Nil
  }

  it should "show shards groupe" in {

    val shardGroups = influx.showShardGroups.futureValue.right.get

    shardGroups should not equal Nil

    shardGroups shouldBe a[Array[_]]

    shardGroups.head shouldBe a[ShardGroupsInfo]

    influx.close() shouldEqual {}
  }
}
