package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.whisk.docker.impl.spotify.DockerKitSpotify
import com.whisk.docker.scalatest.DockerTestKit
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 26.07.17
  */
class DataManagementSpec
  extends FlatSpec
    with Matchers
    with DockerTestKit
    with DockerKitSpotify
    with DockerInfluxService {

  implicit val pc = PatienceConfig(Span(20, Seconds), Span(1, Second))

  "Influx container" should "get up and run correctly" in {
    // CHECKING CONTAINER
    isContainerReady(influxdbContainer).futureValue shouldBe true
    influxdbContainer.getPorts().futureValue.get(8086) should not be None
    influxdbContainer.getIpAddresses().futureValue should not be Seq.empty
  }

  "Data management operation" should "correctly work" in {
    val influx = InfluxClient(influxdbContainer.getIpAddresses().futureValue.head)

    influx.createDatabase("mydb")
  }
 }
