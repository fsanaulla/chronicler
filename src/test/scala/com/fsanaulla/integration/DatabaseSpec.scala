package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.model._
import com.fsanaulla.utils.SampleEntitys._
import com.fsanaulla.utils.TestHelper._
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
    influxdbContainer.getPorts().futureValue.get(dockerPort) should not be None
    influxdbContainer.getIpAddresses().futureValue should not be Seq.empty
  }

  "Database operation" should "correctly work" in {

    val point1 = Point("test2")
      .addTag("firstName", "Martin")
      .addTag("lastName", "Odersky")
      .addField("age", 54)

    val point2 = Point("test2")
      .addTag("firstName", "Jame")
      .addTag("lastName", "Franko")
      .addField("age", 36)

    // INIT INFLUX CLIENT
    val influx = InfluxClient(influxdbContainer.getIpAddresses().futureValue.head, dockerPort)

    // CREATING DB TEST
    influx.createDatabase("mydb").futureValue shouldEqual OkResult

    // DATABASE
    val db = influx.use("mydb")
    val notExistedDb = influx.use("unknown_db")

    notExistedDb.write("test", singleEntity).recover {
      case ex: InfluxException => ex.getMessage shouldEqual "database not found: \"unknown_db\""
    }

    // WRITE - READ TEST
    db.writeFromFile("src/test/resources/points.txt").futureValue shouldEqual NoContentResult
    db.readJs("SELECT * FROM test1").futureValue.queryResult.size shouldEqual 3

    db.writePoint(point1).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test2").futureValue.queryResult shouldEqual Seq(FakeEntity("Martin", "Odersky", 54))

    db.bulkWritePoints(Seq(point1, point2)).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test2").futureValue.queryResult shouldEqual Seq(FakeEntity("Martin", "Odersky", 54), FakeEntity("Jame", "Franko", 36), FakeEntity("Martin", "Odersky", 54))

    db.write("test", singleEntity).futureValue shouldEqual NoContentResult

    db.read[FakeEntity]("SELECT * FROM test").futureValue.queryResult shouldEqual Seq(singleEntity)
    db.readJs("SELECT * FROM test").futureValue.queryResult shouldEqual Seq(singleJsonEntity)

    db.bulkWrite("test", multiEntitys).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test").futureValue.queryResult.sortBy(_.age) shouldEqual (singleEntity +: multiEntitys).sortBy(_.age)
    db.readJs("SELECT * FROM test").futureValue.queryResult shouldEqual multiJsonEntity

    val multiQuery = db.bulkReadJs(Seq("SELECT * FROM test", "SELECT * FROM test WHERE age > 25")).futureValue

    multiQuery.queryResult.size shouldEqual 2
    multiQuery.queryResult shouldBe a [Seq[_]]

    multiQuery.queryResult.head.size shouldEqual 3
    multiQuery.queryResult.head shouldBe a [Seq[_]]
    multiQuery.queryResult.head.head shouldBe a [JsArray]

    multiQuery.queryResult.last.size shouldEqual 2
    multiQuery.queryResult.last shouldBe a [Seq[_]]
    multiQuery.queryResult.last.head shouldBe a [JsArray]

    multiQuery.queryResult shouldEqual largeMultiJsonEntity

    // DROP DB TEST
    influx.dropDatabase("mydb").futureValue shouldEqual OkResult
  }
}
