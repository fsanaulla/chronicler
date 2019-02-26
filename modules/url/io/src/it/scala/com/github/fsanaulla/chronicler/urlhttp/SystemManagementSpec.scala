package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
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

  it should "ping InfluxDB" in {
    val result = influx.ping().success.value
    result.code shouldEqual 204
    result.build.get shouldEqual "OSS"
    result.version.get shouldEqual version
    result.isSuccess shouldBe true
    result.isVerbose shouldBe false
  }

  it should "ping InfluxDB verbose" in {
    val supported = version == "1.7.3"
    val result = influx.ping(supported).success.value
    result.code shouldEqual (if (supported) 200 else 204)
    result.build.get shouldEqual "OSS"
    result.version.get shouldEqual version
    result.isSuccess shouldBe true
    result.isVerbose shouldBe true
  }
}
