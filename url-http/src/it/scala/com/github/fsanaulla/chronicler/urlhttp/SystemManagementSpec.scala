package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers.NoContentResult
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers
import com.github.fsanaulla.chronicler.urlhttp.clients.UrlManagementClient
import org.scalatest.TryValues

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec extends FlatSpecWithMatchers with DockerizedInfluxDB with TryValues {

  lazy val influx: UrlManagementClient =
    Influx.management(host, port, Some(creds))

  "System api" should "ping InfluxDB" in {
    influx.ping.success.value shouldEqual NoContentResult
  }
}
