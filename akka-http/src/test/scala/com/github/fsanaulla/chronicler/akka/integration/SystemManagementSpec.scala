package com.github.fsanaulla.chronicler.akka.integration

import com.github.fsanaulla.chronicler.akka.{InfluxAkkaHttpClient, InfluxDB}
import com.github.fsanaulla.core.test.utils.ResultMatchers.NoContentResult
import com.github.fsanaulla.core.test.utils.{EmptyCredentials, TestSpec}
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec
  extends TestSpec
    with EmptyCredentials
    with EmbeddedInfluxDB {

  lazy val influx: InfluxAkkaHttpClient =
    InfluxDB(host = influxHost, port = httpPort)

  "System api" should "correctly work" in {
    influx.ping().futureValue shouldEqual NoContentResult
  }
}
