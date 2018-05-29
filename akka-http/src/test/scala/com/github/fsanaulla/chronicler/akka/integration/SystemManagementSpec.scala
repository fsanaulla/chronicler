package com.github.fsanaulla.chronicler.akka.integration

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.utils.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.akka.{Influx, InfluxAkkaHttpClient}
import com.github.fsanaulla.core.test.ResultMatchers._
import com.github.fsanaulla.core.test.TestSpec

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec extends TestKit(ActorSystem()) with TestSpec with DockerizedInfluxDB {

  lazy val influx: InfluxAkkaHttpClient =
    Influx.connect(host = host, port = port, system = system, credentials = Some(creds))

  "System api" should "ping InfluxDB" in {
    influx.ping.futureValue shouldEqual NoContentResult
  }
}
