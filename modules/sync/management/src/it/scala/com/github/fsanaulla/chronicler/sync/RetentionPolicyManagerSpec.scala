package com.github.fsanaulla.chronicler.sync

import com.github.fsanaulla.chronicler.core.duration._
import com.github.fsanaulla.chronicler.core.management.rp.RetentionPolicyInfo
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.sync.management.{InfluxMng, SyncManagementClient}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import com.github.fsanaulla.chronicler.testing.BaseSpec
import org.scalatest.{EitherValues, TryValues, BeforeAndAfterAll}

/** Created by Author: fayaz.sanaulla@gmail.com Date: 27.07.17
  */
class RetentionPolicyManagerSpec
    extends BaseSpec
    with EitherValues
    with TryValues
    with DockerizedInfluxDB
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    influx.close()
    super.afterAll()
  }

  val rpDB = "db"

  lazy val influx: SyncManagementClient =
    InfluxMng(host, port, Some(credentials))

  "Retention policy API" - {
    "should" - {
      "create retention policy" in {
        influx.createDatabase(rpDB).success.value.value shouldEqual 200

        influx.showDatabases.success.value.value.contains(rpDB) shouldEqual true

        influx
          .createRetentionPolicy("test", rpDB, 2.hours, 2, Some(2.hours), default = true)
          .success
          .value
          .value shouldEqual 200

        influx
          .showRetentionPolicies(rpDB)
          .success
          .value
          .value
          .contains(
            RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true)
          ) shouldEqual true

      }

      "drop retention policy" in {
        influx.dropRetentionPolicy("autogen", rpDB).success.value.value shouldEqual 200

        influx.showRetentionPolicies(rpDB).success.value.value shouldEqual Seq(
          RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true)
        )
      }

      "update retention policy" in {
        influx
          .updateRetentionPolicy("test", rpDB, Some(3.hours))
          .success
          .value
          .value shouldEqual 200

        influx.showRetentionPolicies(rpDB).success.value.value shouldEqual Seq(
          RetentionPolicyInfo("test", "3h0m0s", "2h0m0s", 2, default = true)
        )
      }

      "clean up everything" in {
        influx.dropRetentionPolicy("test", rpDB).success.value.value shouldEqual 200

        influx.showRetentionPolicies(rpDB).success.value.value.toList shouldEqual Nil

        influx.dropDatabase(rpDB).success.value.value shouldEqual 200

        influx.showDatabases.success.value.value.contains(rpDB) shouldEqual false
      }
    }

  }
}
