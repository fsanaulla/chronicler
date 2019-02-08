package com.github.fsanaulla.chronicler.ahc.management

import com.github.fsanaulla.chronicler.core.duration._
import com.github.fsanaulla.chronicler.core.model.RetentionPolicyInfo
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class RetentionPolicyManagerSpec extends FlatSpec with Matchers with DockerizedInfluxDB with Futures {

  val rpDB = "db"

  lazy val influx: AhcManagementClient =
    InfluxMng(host, port, Some(creds))

  it should "create retention policy" in {
    influx.createDatabase(rpDB).futureValue shouldEqual OkResult

    influx.showDatabases()
      .futureValue
      .queryResult
      .contains(rpDB) shouldEqual true

    influx.createRetentionPolicy("test", rpDB, 2 hours, 2, Some(2 hours), default = true).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.queryResult.contains(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true)) shouldEqual true

  }

  it should "drop retention policy" in {
    influx.dropRetentionPolicy("autogen", rpDB).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB)
      .futureValue
      .queryResult shouldEqual Array(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true))
  }

  it should "update retention policy" in {
    influx.updateRetentionPolicy("test", rpDB, Some(3 hours)).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.queryResult shouldEqual Array(RetentionPolicyInfo("test", "3h0m0s", "2h0m0s", 2, default = true))
  }

  it should "clean up everything" in {
    influx.dropRetentionPolicy("test", rpDB).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.queryResult shouldEqual Nil

    influx.dropDatabase(rpDB).futureValue shouldEqual OkResult

    influx.showDatabases().futureValue.queryResult.contains(rpDB) shouldEqual false
  }

  it should "clear up after all" in {
    influx.close() shouldEqual {}
  }
}
