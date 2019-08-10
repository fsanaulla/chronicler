package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.core.model.ShardGroupsInfo
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import com.github.fsanaulla.chronicler.urlhttp.management.{InfluxMng, UrlManagementClient}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class ShardManagementSpec extends FlatSpec with Matchers with Futures with DockerizedInfluxDB {

  val testDb = "_internal"

  lazy val influx: UrlManagementClient =
    InfluxMng(host, port, Some(creds))

  "Shard Management API" should "show shards" in {

    influx.createDatabase(testDb, shardDuration = Some("1s")).get.right.get shouldEqual 200

    val shards = influx.showShards.get.right.get

    shards should not be Nil
  }

  it should "show shards groupe" in {

    val shardGroups = influx.showShardGroups.get.right.get

    shardGroups should not equal Nil

    shardGroups shouldBe a[Array[_]]

    shardGroups.head shouldBe a[ShardGroupsInfo]

    influx.close() shouldEqual {}
  }
}
