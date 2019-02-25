package com.github.fsanaulla.chronicler.ahc.io.it

import com.github.fsanaulla.chronicler.ahc.io.{AhcIOClient, InfluxIO}
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec extends FlatSpec with Matchers with DockerizedInfluxDB with Futures {

  lazy val influx: AhcIOClient =
    InfluxIO(host, port, Some(creds))

  it should "ping InfluxDB" in {
    val result = influx.ping().futureValue
    result.code shouldEqual 204
    result.build.get shouldEqual "OSS"
    result.version.get shouldEqual version
    result.isSuccess shouldBe true
    result.isVerbose shouldBe false
  }

  it should "ping InfluxDB verbose" in {
    val result = influx.ping(true).futureValue
    result.code shouldEqual 200
    result.build.get shouldEqual "OSS"
    result.version.get shouldEqual version
    result.isSuccess shouldBe true
    result.isVerbose shouldBe true
  }
}
