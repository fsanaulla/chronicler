package com.fsanaulla.integration

import akka.http.scaladsl.model.StatusCodes
import com.fsanaulla.Helper._
import com.fsanaulla.InfluxClient
import com.fsanaulla.SamplesEntity._
import com.whisk.docker.impl.spotify.DockerKitSpotify
import com.whisk.docker.scalatest.DockerTestKit
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers}
import spray.json.{JsArray, JsNumber, JsString}

/**
  * Created by fayaz on 06.07.17.
  */
class DatabaseSpec
  extends FlatSpec
  with Matchers
  with DockerTestKit
  with DockerKitSpotify
  with DockerInfluxService {

  implicit val pc = PatienceConfig(Span(20, Seconds), Span(1, Second))

  "Database operation" should "correctly work" in {

    lazy val host = influxdbContainer.getIpAddresses().futureValue
    lazy val port = influxdbContainer.getPorts().futureValue.get(8086)

    // CHECKING CONTAINER
    isContainerReady(influxdbContainer).futureValue shouldBe true
    port should not be None
    host should not be Seq.empty

    // INIT INFLUX CLIENT
    val influx = new InfluxClient(host.head, 8086)

    // CREATING DB TEST
    influx.createDatabase("mydb").futureValue.status shouldEqual StatusCodes.OK

    // DATABASE
    val db = influx.use("mydb")

    // WRITE - READ TEST
    db.write("test", singleEntity).futureValue.status shouldEqual StatusCodes.NoContent
    db.read[FakeEntity]("SELECT * FROM test").futureValue shouldEqual Seq(singleEntity)
    db.readPure("SELECT * FROM test").futureValue shouldEqual Seq(singleJsonEntity)

    db.bulkWrite("test", multiEntitys).futureValue.status shouldEqual StatusCodes.NoContent
    db.read[FakeEntity]("SELECT * FROM test").futureValue.sortBy(_.age) shouldEqual (singleEntity +: multiEntitys).sortBy(_.age)
    db.readPure("SELECT * FROM test").futureValue shouldEqual multiJsonEntity

    val multiQuery = db.bulkRead(Seq("SELECT * FROM test", "SELECT * FROM test WHERE age > 25")).futureValue

    multiQuery.size shouldEqual 2
    multiQuery shouldBe a [Seq[_]]

    multiQuery.head.size shouldEqual 3
    multiQuery.head shouldBe a [Seq[_]]
    multiQuery.head.head shouldBe a [JsArray]

    multiQuery.last.size shouldEqual 2
    multiQuery.last shouldBe a [Seq[_]]
    multiQuery.last.head shouldBe a [JsArray]

    multiQuery shouldEqual largeMultiJsonEntity

    // DROP DB TEST
    influx.dropDatabase("mydb").futureValue.status shouldEqual StatusCodes.OK
  }
}
