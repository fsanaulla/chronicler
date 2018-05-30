package com.github.fsanaulla.chronicler.urlhttp.integration

import com.github.fsanaulla.chronicler.testing.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.{DockerizedInfluxDB, TestSpec}
import com.github.fsanaulla.chronicler.urlhttp.{Influx, InfluxUrlHttpClient}
import org.scalatest.TryValues

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec extends TestSpec with DockerizedInfluxDB with TryValues {

  lazy val influx: InfluxUrlHttpClient =
    Influx.connect(host, port, Some(creds))

  "System api" should "ping InfluxDB" in {
    influx.ping.success.value shouldEqual NoContentResult
  }
}
