package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.model.{DatabaseInfo, RetentionPolicyInfo}
import com.fsanaulla.utils.Extension._
import com.fsanaulla.utils.InfluxDuration._
import com.fsanaulla.utils.TestHelper.OkResult

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class RetentionPolicyManagerSpec extends IntegrationSpec {

  val rpDB = "rp_db"

  "retention policy operation" should "correctly work" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClient(host)

    // CREATING DB TEST
    influx.createDatabase(rpDB).sync shouldEqual OkResult

    influx.showDatabases().sync.queryResult.contains(DatabaseInfo(rpDB)) shouldEqual true

    influx.createRetentionPolicy("test", rpDB, 2 hours, 2, Some(2 hours), default = true).sync shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).sync.queryResult.contains(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true)) shouldEqual true

    influx.dropRetentionPolicy("autogen", rpDB).sync shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).sync.queryResult shouldEqual Seq(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true))

    influx.updateRetentionPolicy("test", rpDB, Some(3 hours)).sync shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).sync.queryResult shouldEqual Seq(RetentionPolicyInfo("test", "3h0m0s", "2h0m0s", 2, default = true))

    influx.dropRetentionPolicy("test", rpDB).sync shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).sync.queryResult shouldEqual Nil

    influx.dropDatabase(rpDB).sync shouldEqual OkResult

    influx.showDatabases().sync.queryResult.contains(DatabaseInfo(rpDB)) shouldEqual false

    influx.close()
  }
}
