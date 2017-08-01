package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.model.{CreateResult, DeleteResult, ResourceNotFoundException, WriteResult}
import com.fsanaulla.utils.Helper._
import com.fsanaulla.utils.SampleEntitys._
import com.whisk.docker.impl.spotify.DockerKitSpotify
import com.whisk.docker.scalatest.DockerTestKit
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers}
import spray.json.{JsArray, JsNumber, JsString}

import scala.concurrent.duration._

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
    influxdbContainer.getPorts().futureValue.get(dockerPort) should not be None
    influxdbContainer.getIpAddresses().futureValue should not be Seq.empty
  }

  "Database operation" should "correctly work" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClient(influxdbContainer.getIpAddresses().futureValue.head, dockerPort)

    // CREATING DB TEST
    influx.createDatabase("mydb").futureValue shouldEqual CreateResult(200, isSuccess = true)

    // DATABASE
    val db = influx.use("mydb")
    val notExistedDb = influx.use("unknown_db")

    val ex = the [ResourceNotFoundException] thrownBy {
      await(notExistedDb.write("test", singleEntity))(1.seconds)
    }

    ex.getMessage shouldEqual "database not found: \"unknown_db\""

    // WRITE - READ TEST
    db.write("test", singleEntity).futureValue shouldEqual WriteResult(204, isSuccess = true)

    db.read[FakeEntity]("SELECT * FROM test").futureValue shouldEqual Seq(singleEntity)
    db.readJs("SELECT * FROM test").futureValue shouldEqual Seq(singleJsonEntity)

    db.bulkWrite("test", multiEntitys).futureValue shouldEqual WriteResult(204, isSuccess = true)
    db.read[FakeEntity]("SELECT * FROM test").futureValue.sortBy(_.age) shouldEqual (singleEntity +: multiEntitys).sortBy(_.age)
    db.readJs("SELECT * FROM test").futureValue shouldEqual multiJsonEntity

    val multiQuery = db.bulkReadJs(Seq("SELECT * FROM test", "SELECT * FROM test WHERE age > 25")).futureValue

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
    influx.dropDatabase("mydb").futureValue shouldEqual DeleteResult(200, isSuccess = true)
  }
}
