package com.github.fsanaulla.chronicler.akka.integration

import com.github.fsanaulla.chronicler.akka.{InfluxAkkaHttpClient, InfluxDB}
import com.github.fsanaulla.core.model.ShardGroupsInfo
import com.github.fsanaulla.core.test.ResultMatchers._
import com.github.fsanaulla.core.test.TestSpec
import com.github.fsanaulla.core.testing.configurations.InfluxHTTPConf
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class ShardManagementSpec extends TestSpec with EmbeddedInfluxDB with InfluxHTTPConf {

  val testDb = "_internal"

  lazy val influx: InfluxAkkaHttpClient = InfluxDB.connect()

  "shard operations" should "show shards" in {

    influx.createDatabase(testDb, shardDuration = Some("1s")).futureValue shouldEqual OkResult

    val shards = influx.showShards().futureValue.queryResult

    shards should not be Nil

    shards.foreach(println)
  }

  it should "show shards groupe" in {

    val shardGroups = influx.showShardGroups().futureValue.queryResult

    shardGroups should not equal Nil

    shardGroups shouldBe a [Seq[_]]

    shardGroups.head shouldBe a [ShardGroupsInfo]

    influx.close() shouldEqual {}
  }
}
