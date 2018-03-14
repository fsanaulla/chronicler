package com.github.fsanaulla.async.integration

import com.github.fsanaulla.async.utils.TestHelper._
import com.github.fsanaulla.chronicler.async.{InfluxAsyncHttpClient, InfluxDB}
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

  lazy val influx: InfluxAsyncHttpClient =
    InfluxDB(host = influxHost, port = httpPort)

  "System api" should "ping InfluxDB" in {
    influx.ping().futureValue shouldEqual NoContentResult
  }
}
