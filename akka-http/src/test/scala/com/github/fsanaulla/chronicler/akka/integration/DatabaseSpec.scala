package com.github.fsanaulla.chronicler.akka.integration

import java.io.File

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.api.Database
import com.github.fsanaulla.chronicler.akka.utils.SampleEntitys._
import com.github.fsanaulla.chronicler.akka.utils.TestHelper._
import com.github.fsanaulla.chronicler.akka.{Influx, InfluxAkkaHttpClient}
import com.github.fsanaulla.chronicler.core.model.Point
import com.github.fsanaulla.chronicler.core.utils.Extensions.RichJValue
import com.github.fsanaulla.chronicler.testing.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.{DockerizedInfluxDB, FutureHandler, TestSpec}
import jawn.ast.{JArray, JNum}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 02.03.18
  */
class DatabaseSpec
  extends TestKit(ActorSystem())
    with TestSpec
    with FutureHandler
    with DockerizedInfluxDB {

  val testDB = "db"

  lazy val influx: InfluxAkkaHttpClient =
    Influx.connect(host = host, port = port, system = system, credentials = Some(creds))

  lazy val db: Database = influx.database(testDB)

  "Database api" should "write from file" in {
    influx.createDatabase(testDB).futureValue shouldEqual OkResult

    db.writeFromFile(new File(getClass.getResource("/points.txt").getPath)).futureValue shouldEqual NoContentResult
    db.readJs("SELECT * FROM test1").futureValue.result.length shouldEqual 3
  }

  it should "write points" in {

    val point1 = Point("test2")
      .addTag("sex", "Male")
      .addTag("firstName", "Martin")
      .addTag("lastName", "Odersky")
      .addField("age", 54)

    val point2 = Point("test2")
      .addTag("sex", "Male")
      .addTag("firstName", "Jame")
      .addTag("lastName", "Franko")
      .addField("age", 36)

    db.writePoint(point1).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test2").futureValue.result shouldEqual Array(FakeEntity("Male", "Martin", "Odersky", 54))

    db.bulkWritePoints(Array(point1, point2)).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test2").futureValue.result shouldEqual Array(FakeEntity("Male", "Martin", "Odersky", 54), FakeEntity("Male", "Jame", "Franko", 36), FakeEntity("Male", "Martin", "Odersky", 54))
  }

  it should "read multiple query results" in {

    val multiQuery = db.bulkReadJs(
      Array(
        "SELECT * FROM test2",
        "SELECT * FROM test2 WHERE age < 40"
      )
    ).futureValue

    multiQuery.result.length shouldEqual 2
    multiQuery.result shouldBe a[Array[_]]

    multiQuery.result.head.length shouldEqual 3
    multiQuery.result.head shouldBe a[Array[_]]
    multiQuery.result.head.head shouldBe a[JArray]

    multiQuery.result.last.length shouldEqual 1
    multiQuery.result.last shouldBe a[Array[_]]
    multiQuery.result.last.head shouldBe a[JArray]

    multiQuery
      .result
      .map(_.map(_.arrayValue.value.tail)) shouldEqual largeMultiJsonEntity.map(_.map(_.arrayValue.value.tail))
  }

  it should "write native represented entities" in {

    db.writeNative("test3,sex=Male,firstName=Jame,lastName=Lannister age=48").futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test3").futureValue.result shouldEqual Array(FakeEntity("Male", "Jame", "Lannister", 48))

    db.bulkWriteNative(Array("test4,sex=Male,firstName=Jon,lastName=Snow age=24", "test4,sex=Female,firstName=Deny,lastName=Targaryen age=25")).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM test4").futureValue.result shouldEqual Array(FakeEntity("Female", "Deny", "Targaryen", 25), FakeEntity("Male", "Jon", "Snow", 24))

  }

  it should "return grouped result by sex and sum of ages" in {

    db
      .bulkWriteNative(Array("test5,sex=Male,firstName=Jon,lastName=Snow age=24", "test5,sex=Male,firstName=Rainer,lastName=Targaryen age=25"))
      .futureValue shouldEqual NoContentResult

    db
      .readJs("SELECT SUM(\"age\") FROM \"test5\" GROUP BY \"sex\"")
      .futureValue
      .groupedResult
      .map { case (k, v) => k.toSeq -> v } shouldEqual Array(Seq("Male") -> JArray(Array(JNum(49))))

    influx.close() shouldEqual {}
  }
}
