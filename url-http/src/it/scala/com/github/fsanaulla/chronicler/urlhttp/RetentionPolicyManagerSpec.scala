package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.core.model.RetentionPolicyInfo
import com.github.fsanaulla.chronicler.core.utils.InfluxDuration._
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers
import org.scalatest.TryValues

import scala.language.postfixOps

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class RetentionPolicyManagerSpec extends FlatSpecWithMatchers with DockerizedInfluxDB with TryValues {

  val rpDB = "db"

  lazy val influx =
    Influx.connect(host, port, Some(creds))

  "Retention policy" should "create retention policy" in {
    influx.createDatabase(rpDB).success.value shouldEqual OkResult

    influx.showDatabases()
      .success.value
      .queryResult
      .contains(rpDB) shouldEqual true

    influx.createRetentionPolicy("test", rpDB, 2 hours, 2, Some(2 hours), default = true).success.value shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).success.value.queryResult.contains(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true)) shouldEqual true

  }

  it should "drop retention policy" in {
    influx.dropRetentionPolicy("autogen", rpDB).success.value shouldEqual OkResult

    influx.showRetentionPolicies(rpDB)
      .success.value
      .queryResult shouldEqual Array(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true))
  }

  it should "update retention policy" in {
    influx.updateRetentionPolicy("test", rpDB, Some(3 hours)).success.value shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).success.value.queryResult shouldEqual Array(RetentionPolicyInfo("test", "3h0m0s", "2h0m0s", 2, default = true))
  }

  it should "clean up everything" in {
    influx.dropRetentionPolicy("test", rpDB).success.value shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).success.value.queryResult shouldEqual Nil

    influx.dropDatabase(rpDB).success.value shouldEqual OkResult

    influx.showDatabases().success.value.queryResult.contains(rpDB) shouldEqual false

    influx.close() shouldEqual {}
  }
}
