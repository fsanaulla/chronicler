package com.github.fsanaulla.chronicler.akka.integration

import java.io.File

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.api.Database
import com.github.fsanaulla.chronicler.akka.utils.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.akka.utils.SampleEntitys._
import com.github.fsanaulla.chronicler.akka.utils.TestHelper._
import com.github.fsanaulla.chronicler.akka.{Influx, InfluxAkkaHttpClient}
import com.github.fsanaulla.core.model.Point
import com.github.fsanaulla.core.test.ResultMatchers._
import com.github.fsanaulla.core.test.TestSpec
import com.github.fsanaulla.core.utils.Extensions.RichJValue
import jawn.ast.JArray

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 02.03.18
  */
class DatabaseSpec
  extends TestKit(ActorSystem())
    with TestSpec
    with DockerizedInfluxDB {

  val testDB = "db"

  lazy val influx: InfluxAkkaHttpClient =
    Influx.connect(host = host, port = port, system = system, credentials = Some(creds))

  lazy val db: Database = influx.database(testDB)

  "Database api" should "write from file" in {
    influx.createDatabase(testDB).futureValue shouldEqual OkResult

    db.writeFromFile(new File(getClass.getResource("/points.txt").getPath)).futureValue shouldEqual NoContentResult
    db.readJs("SELECT * FROM test1").futureValue.queryResult.length shouldEqual 3
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
    db.read[FakeEntity]("SELECT * FROM test2").futureValue.queryResult shouldEqual Array(FakeEntity("Martin", "Odersky", 54))

    db.bulkWritePoints(Array(point1, point2)).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test2").futureValue.queryResult shouldEqual Array(FakeEntity("Martin", "Odersky", 54), FakeEntity("Jame", "Franko", 36), FakeEntity("Martin", "Odersky", 54))
  }

  it should "read multiple query results" in {

    val multiQuery = db.bulkReadJs(
      Array(
        "SELECT * FROM test2",
        "SELECT * FROM test2 WHERE age < 40"
      )
    ).futureValue

    multiQuery.queryResult.length shouldEqual 2
    multiQuery.queryResult shouldBe a[Array[_]]

    multiQuery.queryResult.head.length shouldEqual 3
    multiQuery.queryResult.head shouldBe a[Array[_]]
    multiQuery.queryResult.head.head shouldBe a[JArray]

    multiQuery.queryResult.last.length shouldEqual 1
    multiQuery.queryResult.last shouldBe a[Array[_]]
    multiQuery.queryResult.last.head shouldBe a[JArray]

    multiQuery
      .queryResult
      .map(_.map(_.arrayValue.value.tail)) shouldEqual largeMultiJsonEntity.map(_.map(_.arrayValue.value.tail))
  }

  it should "write native represented entities" in {

    db.writeNative("test3,firstName=Jame,lastName=Lannister age=48").futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test3").futureValue.queryResult shouldEqual Array(FakeEntity("Jame", "Lannister", 48))

    db.bulkWriteNative(Array("test4,firstName=Jon,lastName=Snow age=24", "test4,firstName=Deny,lastName=Targaryen age=25")).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test4").futureValue.queryResult shouldEqual Array(FakeEntity("Deny", "Targaryen", 25), FakeEntity("Jon", "Snow", 24))

    influx.close() shouldEqual {}
  }
}
