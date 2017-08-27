package com.fsanaulla.integration

import com.fsanaulla.InfluxClientsFactory
import com.fsanaulla.model._
import com.fsanaulla.utils.SampleEntitys._
import com.fsanaulla.utils.TestHelper._
import com.fsanaulla.utils.TestSpec
import spray.json.{JsArray, JsNumber, JsString}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by fayaz on 06.07.17.
  */
class DatabaseSpec extends TestSpec {

  val testDB = "db"

  "Auth database operation" should "correctly work" in {

    val point1 = Point("test2")
      .addTag("firstName", "Martin")
      .addTag("lastName", "Odersky")
      .addField("age", 54)

    val point2 = Point("test2")
      .addTag("firstName", "Jame")
      .addTag("lastName", "Franko")
      .addField("age", 36)

    // INIT INFLUX CLIENT
    val influx = InfluxClientsFactory.createHttpClient(host = influxHost, username = credentials.username, password = credentials.password)

    // CREATING DB TEST
    influx.createDatabase(testDB).futureValue shouldEqual OkResult

    // DATABASE
    val db = influx.use(testDB)
    val notExistedDb = influx.use("unknown_db")

    notExistedDb.write("test", singleEntity).futureValue.ex.value shouldBe a [ResourceNotFoundException]

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

    db.writeNative("meas3,firstName=Jame,lastName=Lannister age=48").futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM meas3").futureValue.queryResult shouldEqual Seq(FakeEntity("Jame", "Lannister", 48))

    db.bulkWriteNative(Seq("meas3,firstName=Jon,lastName=Snow age=24", "meas3,firstName=Deny,lastName=Targaryen age=25")).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM meas3").futureValue.queryResult shouldEqual Seq(FakeEntity("Jame", "Lannister", 48), FakeEntity("Jon", "Snow", 24), FakeEntity("Deny", "Targaryen", 25))

    // DROP DB TEST
    influx.dropDatabase(testDB).futureValue shouldEqual OkResult

    influx.close()
  }
}
