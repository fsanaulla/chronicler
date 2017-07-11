package com.fsanaulla.integration

import akka.http.scaladsl.model.StatusCodes
import com.fsanaulla.Helper._
import com.fsanaulla.InfluxDBClient
import com.whisk.docker.impl.spotify.DockerKitSpotify
import com.whisk.docker.scalatest.DockerTestKit
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._


/**
  * Created by fayaz on 06.07.17.
  */
class InfluxDBSpec
  extends FlatSpec
  with Matchers
  with DockerTestKit
  with DockerKitSpotify
  with DockerInfluxService {

  implicit val pc = PatienceConfig(Span(20, Seconds), Span(1, Second))
  implicit val timeout: FiniteDuration = 1 second

  val influx = new InfluxDBClient("172.17.0.2", 8086)

  "Influxdb database" should "be ready with log line checker" in {
    isContainerReady(influxdbContainer).futureValue shouldBe true
    influxdbContainer.getPorts().futureValue.get(8086) should not be empty
    influxdbContainer.getIpAddresses().futureValue should not be Seq("")
  }

  "Influxdb client" should "successfully create db" in {
    await(influx.createDatabase("mydb")).status shouldEqual StatusCodes.OK
  }

  "Influxdb client" should "successfully drop db" in {
    await(influx.dropDatabase("mydb")).status shouldEqual StatusCodes.OK
  }

}
