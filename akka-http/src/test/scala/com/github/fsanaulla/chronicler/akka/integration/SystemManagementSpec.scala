package com.github.fsanaulla.chronicler.akka.integration

import com.github.fsanaulla.chronicler.akka.InfluxClientFactory
import com.github.fsanaulla.chronicler.akka.utils.TestHelper._
import com.github.fsanaulla.core.test.utils.TestSpec

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec extends TestSpec {
  "System api" should "correctly work" in {
    val influx = InfluxClientFactory.createHttpClient(
      influxHost,
      username = credentials.username,
      password = credentials.password)

    influx.ping().futureValue shouldEqual NoContentResult
  }
}
