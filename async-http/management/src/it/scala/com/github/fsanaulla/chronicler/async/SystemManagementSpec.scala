package com.github.fsanaulla.chronicler.async

import com.github.fsanaulla.chronicler.async.management.{AsyncManagementClient, InfluxMng}
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers.NoContentResult
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec extends FlatSpecWithMatchers with DockerizedInfluxDB with Futures {

  lazy val influx: AsyncManagementClient =
    InfluxMng.apply(host, port, Some(creds))

  "System api" should "ping InfluxDB" in {
    influx.ping.futureValue shouldEqual NoContentResult
  }
}
