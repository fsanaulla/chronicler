package com.github.fsanaulla.integration

import com.github.fsanaulla.InfluxAkkaHttpClient
import com.github.fsanaulla.utils.TestHelper._
import com.github.fsanaulla.utils.TestSpec

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec extends TestSpec {
  "System api" should "correctly work" in {
    val influx = new InfluxAkkaHttpClient(
      influxHost,
      username = credentials.username,
      password = credentials.password)

    influx.ping().futureValue shouldEqual NoContentResult
  }
}
