package com.github.fsanaulla.chronicler.akka.integration

import com.github.fsanaulla.chronicler.akka.InfluxClientFactory
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

  override def httpPort = 9001
  override def backUpPort: Int = httpPort + 1

  "System api" should "correctly work" in {
    val influx = InfluxClientFactory.createHttpClient(
      influxHost,
      port = httpPort,
      username = credentials.username,
      password = credentials.password)

    influx.ping().futureValue shouldEqual NoContentResult
  }
}
