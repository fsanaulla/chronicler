package com.github.fsanaulla.chronicler.akka

import _root_.akka.actor.ActorSystem
import _root_.akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.clients.AkkaFullClient
import com.github.fsanaulla.chronicler.core.model.ShardGroupsInfo
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class ShardManagementSpec
  extends TestKit(ActorSystem())
    with FlatSpecWithMatchers
    with Futures
    with DockerizedInfluxDB {

  val testDb = "_internal"

  lazy val influx: AkkaFullClient =
    Influx(host, port, Some(creds))

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
