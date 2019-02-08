package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers.NoContentResult
import com.github.fsanaulla.chronicler.urlhttp.management.{InfluxMng, UrlManagementClient}
import org.scalatest.{FlatSpec, Matchers, TryValues}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec extends FlatSpec with Matchers with DockerizedInfluxDB with TryValues {

  lazy val influx: UrlManagementClient =
    InfluxMng.apply(host, port, Some(creds))

  "System api" should "ping InfluxDB" in {
    influx.ping.success.value shouldEqual NoContentResult
  }
}
