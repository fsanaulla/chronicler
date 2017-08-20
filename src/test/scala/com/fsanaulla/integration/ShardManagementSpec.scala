package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.utils.InfluxDuration._
import com.fsanaulla.utils.TestHelper._
import com.fsanaulla.utils.TestSpec

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class ShardManagementSpec extends TestSpec {

  val testRp = "test_rp"
  val testDb = "test_db"
  val time: String = 1.hours + 15.minutes

  "shard operations" should "correctly work" in {
    // INIT INFLUX CLIENT
    val influx = InfluxClient(host = influxHost, username = credentials.username, password = credentials.password)

    influx.createDatabase(testDb).futureValue shouldEqual OkResult

    influx.createRetentionPolicy(testRp, testDb, time, 1, Some(time)).futureValue shouldEqual OkResult

    val shards = influx.showShards().futureValue.queryResult.filter(_.shards.nonEmpty).head.shards

    shards.size should not equal 0

    influx.dropShard(shards.head.id).futureValue shouldEqual OkResult

    influx.showShards().futureValue.queryResult.filter(_.shards.nonEmpty).head.shards.find(_.id == shards.head.id) shouldEqual None

    influx.dropRetentionPolicy(testRp, testDb).futureValue shouldEqual OkResult

    influx.dropDatabase(testDb).futureValue shouldEqual OkResult

    influx.close()
  }
}
