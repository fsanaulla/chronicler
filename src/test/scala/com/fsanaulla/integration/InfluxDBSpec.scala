package com.fsanaulla.integration

import akka.http.scaladsl.model.StatusCodes
import com.fsanaulla.InfluxDBClient
import com.fsanaulla.integration.Samples._
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

  "Influxdb client" should "correctly work" in {

    lazy val host = influxdbContainer.getIpAddresses().futureValue
    lazy val port = influxdbContainer.getPorts().futureValue.get(8086)

    // CHECKING CONTAINER
    isContainerReady(influxdbContainer).futureValue shouldBe true
    port should not be None
    host should not be Seq.empty

    // INIT INFLUX CLIENT
    val influx = new InfluxDBClient(host.head, 8086)

    // CREATING DB TEST
    influx.createDatabase("mydb").futureValue.status shouldEqual StatusCodes.OK

    // DATABASE
    val db = influx.use("mydb")

    // WRITE
    db.write("test", singleEntity).futureValue.status shouldEqual StatusCodes.NoContent

    // READ
    db.read[FakeEntity]("SELECT * FROM test").futureValue shouldEqual Seq(singleEntity)

    // DROP DB TEST
    influx.dropDatabase("mydb").futureValue.status shouldEqual StatusCodes.OK
  }
}
