package com.github.fsanaulla.chronicler.ahc.management

import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec
  extends FlatSpec
    with Matchers
    with Futures
    with DockerizedInfluxDB {

  lazy val influx: AhcManagementClient =
    InfluxMng(host, port, Some(creds))

  "System Management API" should "ping InfluxDB" in {
    val result = influx.ping.futureValue.right.get
    result.build shouldEqual "OSS"
    result.version shouldEqual version
  }
}
