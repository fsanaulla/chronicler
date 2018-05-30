package com.github.fsanaulla.chronicler.async.integration

import com.github.fsanaulla.chronicler.async.{Influx, InfluxAsyncHttpClient}
import com.github.fsanaulla.chronicler.testing.{DockerizedInfluxDB, FutureHandler, TestSpec}
import com.github.fsanaulla.chronicler.testing.ResultMatchers._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec extends TestSpec with DockerizedInfluxDB with FutureHandler {

  lazy val influx: InfluxAsyncHttpClient =
    Influx.connect(host, port, Some(creds))

  "System api" should "ping InfluxDB" in {
    influx.ping().futureValue shouldEqual NoContentResult
  }
}
