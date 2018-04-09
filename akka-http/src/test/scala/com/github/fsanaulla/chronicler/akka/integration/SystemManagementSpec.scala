package com.github.fsanaulla.chronicler.akka.integration

import com.github.fsanaulla.chronicler.akka.{InfluxAkkaHttpClient, InfluxDB}
import com.github.fsanaulla.core.test.ResultMatchers._
import com.github.fsanaulla.core.test.TestSpec
import com.github.fsanaulla.core.testing.configurations.InfluxHTTPConf
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec extends TestSpec with EmbeddedInfluxDB with InfluxHTTPConf {

  lazy val influx: InfluxAkkaHttpClient = InfluxDB.connect()

  "System api" should "ping InfluxDB" in {
    influx.ping().futureValue shouldEqual NoContentResult
  }
}
