package com.github.fsanaulla.chronicler.sync

import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.sync.management.{InfluxMng, SyncManagementClient}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{EitherValues, TryValues, BeforeAndAfterAll}
import com.github.fsanaulla.chronicler.testing.BaseSpec

/** Created by Author: fayaz.sanaulla@gmail.com Date: 07.09.17
  */
class SystemManagementSpec
    extends BaseSpec
    with EitherValues
    with TryValues
    with DockerizedInfluxDB
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    influx.close()
    super.afterAll()
  }

  lazy val influx: SyncManagementClient =
    InfluxMng(host, port, Some(credentials))

  "System Management API" - {
    "should" - {
      "ping InfluxDB" in {
        val result = influx.ping.success.value.value
        result.build shouldEqual "OSS"
        result.version shouldEqual version
      }
    }
  }
}
