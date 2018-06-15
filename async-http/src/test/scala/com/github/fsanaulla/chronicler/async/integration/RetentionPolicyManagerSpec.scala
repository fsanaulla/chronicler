package com.github.fsanaulla.chronicler.async.integration

import com.github.fsanaulla.chronicler.async.{Influx, InfluxAsyncHttpClient}
import com.github.fsanaulla.chronicler.core.model.RetentionPolicyInfo
import com.github.fsanaulla.chronicler.core.utils.InfluxDuration._
import com.github.fsanaulla.chronicler.testing.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.{DockerizedInfluxDB, FutureHandler, TestSpec}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class RetentionPolicyManagerSpec extends TestSpec with DockerizedInfluxDB with FutureHandler {

  val rpDB = "db"

  lazy val influx: InfluxAsyncHttpClient =
    Influx.connect(host, port, Some(creds))

  "Retention policy" should "create retention policy" in {
    influx.createDatabase(rpDB).futureValue shouldEqual OkResult

    influx.showDatabases()
      .futureValue
      .result
      .contains(rpDB) shouldEqual true

    influx.createRetentionPolicy("test", rpDB, 2 hours, 2, Some(2 hours), default = true).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.result.contains(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true)) shouldEqual true

  }

  it should "drop retention policy" in {
    influx.dropRetentionPolicy("autogen", rpDB).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB)
      .futureValue
      .result shouldEqual Array(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true))
  }

  it should "update retention policy" in {
    influx.updateRetentionPolicy("test", rpDB, Some(3 hours)).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.result shouldEqual Array(RetentionPolicyInfo("test", "3h0m0s", "2h0m0s", 2, default = true))
  }

  it should "clean up everything" in {
    influx.dropRetentionPolicy("test", rpDB).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.result shouldEqual Nil

    influx.dropDatabase(rpDB).futureValue shouldEqual OkResult

    influx.showDatabases().futureValue.result.contains(rpDB) shouldEqual false
  }

  it should "clear up after all" in {
    influx.close() shouldEqual {}
  }
}
