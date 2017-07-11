package com.fsanaulla.integration

import akka.http.scaladsl.model.StatusCodes
import com.fsanaulla.InfluxDBClient
import com.whisk.docker.impl.spotify.DockerKitSpotify
import com.whisk.docker.scalatest.DockerTestKit
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers}

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
  val influx = new InfluxDBClient("172.17.0.2", 8086)


  "Influxdb container" should "be ready" in {
    isContainerReady(influxdbContainer).futureValue shouldBe true
    influxdbContainer.getPorts().futureValue.get(8086) should not be empty
    influxdbContainer.getIpAddresses().futureValue should not be Seq.empty
  }

  behavior of "Influxdb client"

  it should "successfully create db" in {
    influx.createDatabase("mydb").futureValue.status shouldEqual StatusCodes.OK
  }

  it should "successfully drop db" in {
    influx.dropDatabase("mydb").futureValue.status shouldEqual StatusCodes.OK
  }

}
