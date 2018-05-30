package com.github.fsanaulla.chronicler.akka.integration

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.{Influx, InfluxAkkaHttpClient}
import com.github.fsanaulla.chronicler.testing.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.{DockerizedInfluxDB, FutureHandler, TestSpec}
import com.github.fsanaulla.core.model.ShardGroupsInfo

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class ShardManagementSpec
  extends TestKit(ActorSystem())
    with TestSpec
    with FutureHandler
    with DockerizedInfluxDB {

  val testDb = "_internal"

  lazy val influx: InfluxAkkaHttpClient =
    Influx.connect(host = host, port = port, system = system, credentials = Some(creds))

  "shard operations" should "show shards" in {

    influx.createDatabase(testDb, shardDuration = Some("1s")).futureValue shouldEqual OkResult

    val shards = influx.showShards.futureValue.queryResult

    shards should not be Nil

    shards.foreach(println)
  }

  it should "show shards groupe" in {

    val shardGroups = influx.showShardGroups.futureValue.queryResult

    shardGroups should not equal Nil

    shardGroups shouldBe a [Array[_]]

    shardGroups.head shouldBe a [ShardGroupsInfo]

    influx.close() shouldEqual {}
  }
}
