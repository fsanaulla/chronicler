package com.github.fsanaulla.chronicler.akka

import _root_.akka.actor.ActorSystem
import _root_.akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.management.{AkkaManagementClient, InfluxMng}
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import org.scalatest.{FlatSpecLike, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec
  extends TestKit(ActorSystem())
    with FlatSpecLike
    with Matchers
    with Futures
    with DockerizedInfluxDB {

  lazy val influx: AkkaManagementClient =
    InfluxMng(host, port, Some(creds))

  it should "ping InfluxDB" in {
    val result = influx.ping().futureValue
    result.code shouldEqual 204
    result.build.get shouldEqual "OSS"
    result.version.get shouldEqual version
    result.isSuccess shouldBe true
    result.isVerbose shouldBe false
  }

  it should "ping InfluxDB verbose" in {
    val supported = version == "1.7.3"
    val result = influx.ping(supported).futureValue
    result.code shouldEqual (if (supported) 200 else 204)
    result.build.get shouldEqual "OSS"
    result.version.get shouldEqual version
    result.isSuccess shouldBe true
    result.isVerbose shouldBe true
  }
}
