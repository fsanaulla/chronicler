package com.fsanaulla.integration

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

    "influxdb database" should "be ready with log line checker" in {
      isContainerReady(influxdbContainer).futureValue shouldBe true
      influxdbContainer.getPorts().futureValue.get(8086) should not be empty
      influxdbContainer.getIpAddresses().futureValue should not be Seq.empty
    }

}
