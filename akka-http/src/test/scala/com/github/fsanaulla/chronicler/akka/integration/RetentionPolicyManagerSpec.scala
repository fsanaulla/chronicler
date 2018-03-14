package com.github.fsanaulla.chronicler.akka.integration

import com.github.fsanaulla.chronicler.akka.InfluxDB
import com.github.fsanaulla.core.model.RetentionPolicyInfo
import com.github.fsanaulla.core.test.utils.ResultMatchers._
import com.github.fsanaulla.core.test.utils.{EmptyCredentials, TestSpec}
import com.github.fsanaulla.core.utils.InfluxDuration._
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class RetentionPolicyManagerSpec
  extends TestSpec
    with EmptyCredentials
    with EmbeddedInfluxDB {

  val rpDB = "db"

  lazy val influx =
    InfluxDB(host = influxHost, port = httpPort)

  "retention policy operation" should "create RP" in {

    influx.createDatabase(rpDB).futureValue shouldEqual OkResult

    influx.showDatabases().futureValue.queryResult.contains(rpDB) shouldEqual true

    influx.createRetentionPolicy("test", rpDB, 2 hours, 2, Some(2 hours), default = true).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.queryResult.contains(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true)) shouldEqual true
  }

  it should "drop RP" in {
    influx.dropRetentionPolicy("autogen", rpDB).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.queryResult shouldEqual Seq(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true))
  }

  it should "update RP" in {
    influx.updateRetentionPolicy("test", rpDB, Some(3 hours)).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.queryResult shouldEqual Seq(RetentionPolicyInfo("test", "3h0m0s", "2h0m0s", 2, default = true))

    influx.close() shouldEqual {}
  }
}
