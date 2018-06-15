package com.github.fsanaulla.chronicler.async.integration

import java.io.File

import com.github.fsanaulla.chronicler.async.api.Database
import com.github.fsanaulla.chronicler.async.utils.SampleEntitys.largeMultiJsonEntity
import com.github.fsanaulla.chronicler.async.utils.TestHelper.FakeEntity
import com.github.fsanaulla.chronicler.async.{Influx, InfluxAsyncHttpClient}
import com.github.fsanaulla.chronicler.core.model.Point
import com.github.fsanaulla.chronicler.core.utils.Extensions.RichJValue
import com.github.fsanaulla.chronicler.testing.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.{DockerizedInfluxDB, FutureHandler, TestSpec}
import jawn.ast.{JArray, JNum}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.09.17
  */
class DatabaseSpec extends TestSpec with FutureHandler with DockerizedInfluxDB {

  val testDB = "db"

  lazy val influx: InfluxAsyncHttpClient =
    Influx.connect(host, port, Some(creds))

  lazy val db: Database = influx.database(testDB)

  "Database API" should "write data from file" in {
    influx.createDatabase(testDB).futureValue shouldEqual OkResult

    db.writeFromFile(new File(getClass.getResource("/points.txt").getPath))
      .futureValue shouldEqual NoContentResult
    
    db.readJs("SELECT * FROM test1")
      .futureValue
      .result
      .length shouldEqual 3
  }

  it should "write 2 points represented entities" in {

    val point1 = Point("test2")
      .addTag("firstName", "Martin")
      .addTag("lastName", "Odersky")
      .addField("age", 54)

    val point2 = Point("test2")
      .addTag("firstName", "Jame")
      .addTag("lastName", "Franko")
      .addField("age", 36)

    db.writePoint(point1).futureValue shouldEqual NoContentResult
    
    db.read[FakeEntity]("SELECT * FROM test2")
      .futureValue
      .result shouldEqual Array(FakeEntity("Martin", "Odersky", 54))

    db.bulkWritePoints(Array(point1, point2)).futureValue shouldEqual NoContentResult
    
    db.read[FakeEntity]("SELECT * FROM test2")
      .futureValue
      .result shouldEqual Array(FakeEntity("Martin", "Odersky", 54), FakeEntity("Jame", "Franko", 36), FakeEntity("Martin", "Odersky", 54))
  }

  it should "retrieve multiple request" in {

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

  it should "write native" in {

    db.writeNative("test3,firstName=Jame,lastName=Lannister age=48").futureValue shouldEqual NoContentResult
    
    db.read[FakeEntity]("SELECT * FROM test3")
      .futureValue
      .result shouldEqual Array(FakeEntity("Jame", "Lannister", 48))

    db.bulkWriteNative(Seq("test4,firstName=Jon,lastName=Snow age=24", "test4,firstName=Deny,lastName=Targaryen age=25")).futureValue shouldEqual NoContentResult

    db.read[FakeEntity]("SELECT * FROM test4")
      .futureValue
      .result shouldEqual Array(FakeEntity("Deny", "Targaryen", 25), FakeEntity("Jon", "Snow", 24))

    influx.close() shouldEqual {}
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
