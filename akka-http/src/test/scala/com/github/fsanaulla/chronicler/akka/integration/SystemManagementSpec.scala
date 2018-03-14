package com.github.fsanaulla.chronicler.akka.integration

import com.github.fsanaulla.chronicler.akka.{InfluxAkkaHttpClient, InfluxDB}
import com.github.fsanaulla.core.test.utils.ResultMatchers.NoContentResult
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec
  extends TestSpec
    with EmbeddedInfluxDB {

  lazy val influx: InfluxAkkaHttpClient = InfluxDB(influxHost)

  "System api" should "ping InfluxDB" in {
    influx.ping().futureValue shouldEqual NoContentResult
  }
}
