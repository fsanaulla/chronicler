package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers.NoContentResult
import com.github.fsanaulla.chronicler.urlhttp.io.{InfluxIO, UrlIOClient}
import org.scalatest.{FlatSpec, Matchers, TryValues}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec extends FlatSpec with Matchers with DockerizedInfluxDB with TryValues {

  lazy val influx: UrlIOClient =
    InfluxIO(host, port, Some(creds))

  "System api" should "ping InfluxDB" in {
    influx.ping.success.value shouldEqual NoContentResult
  }
}
