package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.utils.Helper._
import com.fsanaulla.utils.SampleEntitys._
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

  "Influx container" should "get up and run correctly" in {
    // CHECKING CONTAINER
    isContainerReady(influxdbContainer).futureValue shouldBe true
    influxdbContainer.getPorts().futureValue.get(8086) should not be None
    influxdbContainer.getIpAddresses().futureValue should not be Seq.empty
  }

  "Database operation" should "correctly work" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClient(influxdbContainer.getIpAddresses().futureValue.head)

    // CREATING DB TEST
    influx.createDatabase("mydb").futureValue.status shouldEqual OK

    // DATABASE
    val db = influx.use("mydb")

    // WRITE - READ TEST
    db.write("test", singleEntity).futureValue.status shouldEqual NoContent
    db.read[FakeEntity]("SELECT * FROM test").futureValue shouldEqual Seq(singleEntity)
    db.readPure("SELECT * FROM test").futureValue shouldEqual Seq(singleJsonEntity)

    db.bulkWrite("test", multiEntitys).futureValue.status shouldEqual NoContent
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
    influx.dropDatabase("mydb").futureValue.status shouldEqual OK
  }
}
