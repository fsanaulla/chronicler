package com.github.fsanaulla.integration

import com.github.fsanaulla.core.model.RetentionPolicyInfo
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.core.utils.InfluxDuration._
import com.github.fsanaulla.utils.TestHelper.OkResult
import com.github.fsanaulla.{InfluxAsyncHttpClient, InfluxClientFactory}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class RetentionPolicyManagerSpec extends TestSpec {

  val rpDB = "async_rp_spec_db"

  lazy val influx: InfluxAsyncHttpClient = InfluxClientFactory.createHttpClient(
    host = influxHost,
    username = credentials.username,
    password = credentials.password
  )

  "Retention policy" should "create retention policy" in {
    influx.createDatabase(rpDB).futureValue shouldEqual OkResult

    influx.showDatabases().futureValue.queryResult.contains(rpDB) shouldEqual true

    influx.createRetentionPolicy("test", rpDB, 2 hours, 2, Some(2 hours), default = true).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.queryResult.contains(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true)) shouldEqual true

  }

  it should "drop retention policy" in {
    influx.dropRetentionPolicy("autogen", rpDB).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.queryResult shouldEqual Seq(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true))
  }

  it should "update retention policy" in {
    influx.updateRetentionPolicy("test", rpDB, Some(3 hours)).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.queryResult shouldEqual Seq(RetentionPolicyInfo("test", "3h0m0s", "2h0m0s", 2, default = true))
  }

  it should "clean up everything" in {
    influx.dropRetentionPolicy("test", rpDB).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.queryResult shouldEqual Nil

    influx.dropDatabase(rpDB).futureValue shouldEqual OkResult

    influx.showDatabases().futureValue.queryResult.contains(rpDB) shouldEqual false
  }

  influx.close()
}
