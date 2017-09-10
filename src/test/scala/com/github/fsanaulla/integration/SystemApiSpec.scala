package com.github.fsanaulla.integration

import com.github.fsanaulla.InfluxClientsFactory
import com.github.fsanaulla.utils.TestHelper._
import com.github.fsanaulla.utils.TestSpec

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemApiSpec extends TestSpec {
  "System api" should "correctly work" in {
    val influx = InfluxClientsFactory.createHttpClient(
      influxHost,
      username = credentials.username,
      password = credentials.password)

    influx.ping().futureValue shouldEqual NoContentResult
  }
}
