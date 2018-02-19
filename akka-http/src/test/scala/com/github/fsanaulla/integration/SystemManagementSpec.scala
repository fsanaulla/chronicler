package com.github.fsanaulla.integration

import com.github.fsanaulla.InfluxClientFactory
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.utils.TestHelper._

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
