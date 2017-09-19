package com.github.fsanaulla.integration

import com.github.fsanaulla.InfluxClientsFactory
import com.github.fsanaulla.model.{Point, ResourceNotFoundException}
import com.github.fsanaulla.utils.JsonSupport._
import com.github.fsanaulla.utils.SampleEntitys._
import com.github.fsanaulla.utils.TestHelper.{FakeEntity, _}
import com.github.fsanaulla.utils.TestSpec
import spray.json.{JsArray, JsNumber, JsString, JsValue}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by fayaz on 06.07.17.
  */
class DatabaseSpec extends TestSpec {

  val testDB = "database_spec_db"

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
    val notExistedMeas = influx.use("unknown_db").measurement[FakeEntity]("test")
    val meas1 = db.measurement[FakeEntity]("test")

    notExistedMeas.write(singleEntity).futureValue.ex.value shouldBe a [ResourceNotFoundException]

    // WRITE - READ TEST
    db.writeFromFile("src/test/resources/points.txt").futureValue shouldEqual NoContentResult
    db.readJs("SELECT * FROM test1").futureValue.queryResult.size shouldEqual 3

    db.writePoint(point1).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test2").futureValue.queryResult shouldEqual Seq(FakeEntity("Martin", "Odersky", 54))

    db.bulkWritePoints(Seq(point1, point2)).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test2").futureValue.queryResult shouldEqual Seq(FakeEntity("Martin", "Odersky", 54), FakeEntity("Jame", "Franko", 36), FakeEntity("Martin", "Odersky", 54))

    meas1.write(singleEntity).futureValue shouldEqual NoContentResult

    db.read[FakeEntity]("SELECT * FROM test").futureValue.queryResult shouldEqual Seq(singleEntity)
    db.readJs("SELECT * FROM test")
      .futureValue
      .queryResult
      .headOption
      .map(_.convertTo[Seq[JsValue]])
      .map(_.tail) shouldEqual Some(singleJsonEntity.convertTo[Seq[JsValue]].tail)

    meas1.bulkWrite(multiEntitys).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test").futureValue.queryResult.sortBy(_.age) shouldEqual (singleEntity +: multiEntitys).sortBy(_.age)
    db.readJs("SELECT * FROM test")
      .futureValue
      .queryResult
      .map(_.convertTo[Seq[JsValue]])
      .map(_.tail) shouldEqual multiJsonEntity.map(_.convertTo[Seq[JsValue]].tail)

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
