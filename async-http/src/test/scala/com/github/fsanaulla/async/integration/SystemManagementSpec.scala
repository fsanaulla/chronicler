package com.github.fsanaulla.async.integration

import com.github.fsanaulla.async.utils.TestHelper._
import com.github.fsanaulla.chronicler.async.InfluxClientFactory
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

  "System api" should "ping InfluxDB" in {
    val influx = InfluxClientFactory.createHttpClient(
      influxHost,
      username = credentials.username,
      password = credentials.password)

    influx.ping().futureValue shouldEqual NoContentResult
  }
}
