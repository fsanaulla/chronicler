package com.github.fsanaulla.chronicler.akka.integration

import java.io.File

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.github.fsanaulla.chronicler.akka.api.Database
import com.github.fsanaulla.chronicler.akka.utils.SampleEntitys._
import com.github.fsanaulla.chronicler.akka.utils.TestHelper._
import com.github.fsanaulla.chronicler.akka.{InfluxAkkaHttpClient, InfluxDB}
import com.github.fsanaulla.core.model.Point
import com.github.fsanaulla.core.test.TestSpec
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB
import spray.json.{DefaultJsonProtocol, JsArray, JsValue}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 02.03.18
  */
class DatabaseSpec
  extends TestSpec
    with SprayJsonSupport
    with DefaultJsonProtocol
    with EmbeddedInfluxDB {

  val testDB = "db"

  lazy val influx: InfluxAkkaHttpClient = InfluxDB.connect()

  lazy val db: Database = influx.database(testDB)

  "Database api" should "write from file" in {
    influx.createDatabase(testDB).futureValue shouldEqual OkResult

    db.writeFromFile(new File(getClass.getResource("/points.txt").getPath)).futureValue shouldEqual NoContentResult
    db.readJs("SELECT * FROM test1").futureValue.queryResult.size shouldEqual 3
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

    db.writePoint(point1).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test2").futureValue.queryResult shouldEqual Seq(FakeEntity("Martin", "Odersky", 54))

    db.bulkWritePoints(Seq(point1, point2)).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test2").futureValue.queryResult shouldEqual Seq(FakeEntity("Martin", "Odersky", 54), FakeEntity("Jame", "Franko", 36), FakeEntity("Martin", "Odersky", 54))
  }

  it should "read multiple query results" in {

    val multiQuery = db.bulkReadJs(
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

  it should "write native represented entities" in {

    db.writeNative("test3,firstName=Jame,lastName=Lannister age=48").futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test3").futureValue.queryResult shouldEqual Seq(FakeEntity("Jame", "Lannister", 48))

    db.bulkWriteNative(Seq("test4,firstName=Jon,lastName=Snow age=24", "test4,firstName=Deny,lastName=Targaryen age=25")).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test4").futureValue.queryResult shouldEqual Seq(FakeEntity("Deny", "Targaryen", 25), FakeEntity("Jon", "Snow", 24))

    influx.close() shouldEqual {}
  }
}
