package com.github.fsanaulla.chronicler.akka.integration

import com.github.fsanaulla.chronicler.akka.InfluxClientFactory
import com.github.fsanaulla.chronicler.akka.utils.TestHelper.OkResult
import com.github.fsanaulla.core.model.RetentionPolicyInfo
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.core.utils.InfluxDuration._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class RetentionPolicyManagerSpec extends TestSpec {

  val rpDB = "rp_spec_db"

  "retention policy operation" should "correctly work" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClientFactory.createHttpClient(host = influxHost, username = credentials.username, password = credentials.password)

    // CREATING DB TEST
    influx.createDatabase(rpDB).futureValue shouldEqual OkResult

    influx.showDatabases().futureValue.queryResult.contains(rpDB) shouldEqual true

    influx.createRetentionPolicy("test", rpDB, 2 hours, 2, Some(2 hours), default = true).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.queryResult.contains(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true)) shouldEqual true

    influx.dropRetentionPolicy("autogen", rpDB).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.queryResult shouldEqual Seq(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true))

    influx.updateRetentionPolicy("test", rpDB, Some(3 hours)).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.queryResult shouldEqual Seq(RetentionPolicyInfo("test", "3h0m0s", "2h0m0s", 2, default = true))

    influx.dropRetentionPolicy("test", rpDB).futureValue shouldEqual OkResult

    influx.showRetentionPolicies(rpDB).futureValue.queryResult shouldEqual Nil

    influx.dropDatabase(rpDB).futureValue shouldEqual OkResult

    influx.showDatabases().futureValue.queryResult.contains(rpDB) shouldEqual false

    influx.close()
  }
}
