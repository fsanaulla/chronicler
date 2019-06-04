package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.urlhttp.io.{InfluxIO, UrlIOClient}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec extends FlatSpec with Matchers with DockerizedInfluxDB {

  lazy val influx: UrlIOClient = InfluxIO(host, port, Some(creds))

  it should "ping InfluxDB" in {
    val result = influx.ping.get.right.get
    result.build shouldEqual "OSS"
    result.version shouldEqual version
  }
}
