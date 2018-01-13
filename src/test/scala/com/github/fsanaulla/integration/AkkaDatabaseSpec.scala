package com.github.fsanaulla.integration

import com.github.fsanaulla.InfluxClientsFactory
import com.github.fsanaulla.api.AkkaDatabase
import com.github.fsanaulla.clients.InfluxAkkaHttpClient
import com.github.fsanaulla.model.Point
import com.github.fsanaulla.utils.JsonSupport._
import com.github.fsanaulla.utils.SampleEntitys._
import com.github.fsanaulla.utils.TestHelper.{FakeEntity, _}
import com.github.fsanaulla.utils.TestSpec
import spray.json.{JsArray, JsValue}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by fayaz on 06.07.17.
  */
class AkkaDatabaseSpec extends TestSpec {

  val testDB = "database_spec_db"

  // INIT INFLUX CLIENT
  lazy val influx: InfluxAkkaHttpClient = InfluxClientsFactory
    .createAkkaHttpClient(
      host = influxHost,
      username = credentials.username,
      password = credentials.password)

  lazy val nonTpSfApi: AkkaDatabase = influx.database(testDB)

  "UnsafelyApi" should "write from file" in {

    // CREATING DB TEST
    influx.createDatabase(testDB).futureValue shouldEqual OkResult

    // WRITE - READ TEST
    nonTpSfApi.writeFromFile("src/test/resources/points.txt").futureValue shouldEqual NoContentResult
//    nonTpSfApi.readJs("SELECT * FROM test1").futureValue.queryResult.size shouldEqual 3
  }

  it should "write points" in {

    val point1 = Point("test2")
      .addTag("firstName", "Martin")
      .addTag("lastName", "Odersky")
      .addField("age", 54)

    val point2 = Point("test2")
      .addTag("firstName", "Jame")
      .addTag("lastName", "Franko")
      .addField("age", 36)

    nonTpSfApi.writePoint(point1).futureValue shouldEqual NoContentResult
    nonTpSfApi.read[FakeEntity]("SELECT * FROM test2").futureValue.queryResult shouldEqual Seq(FakeEntity("Martin", "Odersky", 54))

    nonTpSfApi.bulkWritePoints(Seq(point1, point2)).futureValue shouldEqual NoContentResult
    nonTpSfApi.read[FakeEntity]("SELECT * FROM test2").futureValue.queryResult shouldEqual Seq(FakeEntity("Martin", "Odersky", 54), FakeEntity("Jame", "Franko", 36), FakeEntity("Martin", "Odersky", 54))
  }

  it should "bulk read js" in {

    val multiQuery = nonTpSfApi.bulkReadJs(
      Seq(
        "SELECT * FROM test2",
        "SELECT * FROM test2 WHERE age < 40"
      )
    ).futureValue

    multiQuery.queryResult.size shouldEqual 2
    multiQuery.queryResult shouldBe a[Seq[_]]

    multiQuery.queryResult.head.size shouldEqual 3
    multiQuery.queryResult.head shouldBe a[Seq[_]]
    multiQuery.queryResult.head.head shouldBe a[JsArray]

    multiQuery.queryResult.last.size shouldEqual 1
    multiQuery.queryResult.last shouldBe a[Seq[_]]
    multiQuery.queryResult.last.head shouldBe a[JsArray]

    multiQuery
      .queryResult
      .map(_.map(_.convertTo[Seq[JsValue]].tail)) shouldEqual largeMultiJsonEntity.map(_.map(_.convertTo[Seq[JsValue]].tail))
  }

  it should "write native" in {

    nonTpSfApi.writeNative("test3,firstName=Jame,lastName=Lannister age=48").futureValue shouldEqual NoContentResult
    nonTpSfApi.read[FakeEntity]("SELECT * FROM test3").futureValue.queryResult shouldEqual Seq(FakeEntity("Jame", "Lannister", 48))

    nonTpSfApi.bulkWriteNative(Seq("test4,firstName=Jon,lastName=Snow age=24", "test4,firstName=Deny,lastName=Targaryen age=25")).futureValue shouldEqual NoContentResult
    nonTpSfApi.read[FakeEntity]("SELECT * FROM test4").futureValue.queryResult shouldEqual Seq(FakeEntity("Deny", "Targaryen", 25), FakeEntity("Jon", "Snow", 24))
  }

  it should "clear up after all" in {

    // DROP DB TEST
    influx.dropDatabase(testDB).futureValue shouldEqual OkResult

    influx.close()
  }
}
