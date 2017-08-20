package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.utils.TestHelper._
import com.fsanaulla.utils.TestSpec

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class ShardManagementSpec extends TestSpec {

  val testDb = "_internal"

  "shard operations" should "correctly work" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClient(host = influxHost, username = credentials.username, password = credentials.password)

//    influx.createDatabase(testDb).futureValue shouldEqual OkResult
//
//    influx.createRetentionPolicy(testRp, testDb, time, 1, Some(time)).futureValue shouldEqual OkResult

    val shards = influx.getShards(testDb).futureValue

    shards.size should not equal 0

    influx.dropShard(shards.head.id).futureValue shouldEqual OkResult

    influx.getShards(testDb).futureValue.size shouldEqual shards.size - 1

    influx.close()
  }
}
