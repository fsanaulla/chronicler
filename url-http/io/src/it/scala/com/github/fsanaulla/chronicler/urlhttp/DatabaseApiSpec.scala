package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.core.model.{InfluxConfig, Point}
import com.github.fsanaulla.chronicler.core.utils.Extensions.RichJValue
import com.github.fsanaulla.chronicler.testing.it.FakeEntity.fmt
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, FakeEntity}
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers
import com.github.fsanaulla.chronicler.urlhttp.SampleEntitys.largeMultiJsonEntity
import com.github.fsanaulla.chronicler.urlhttp.io.api.Database
import com.github.fsanaulla.chronicler.urlhttp.io.{UrlIOClient, InfluxIO => IOInflux}
import com.github.fsanaulla.chronicler.urlhttp.management.{UrlManagementClient, InfluxMng => MngInflux}
import jawn.ast.{JArray, JNum}
import org.scalatest.{OptionValues, TryValues}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.09.17
  */
class DatabaseApiSpec extends FlatSpecWithMatchers with DockerizedInfluxDB with TryValues with OptionValues {

  val testDB = "db"

  lazy val influxConf =
    InfluxConfig(host, port, credentials = Some(creds), gzipped = false)

  lazy val mng: UrlManagementClient =
    MngInflux(influxConf)

  lazy val io: UrlIOClient =
    IOInflux(influxConf)

  lazy val db: Database = io.database(testDB)

  "Database API" should "write data from file" in {
    mng.createDatabase(testDB).success.value shouldEqual OkResult

    db.writeFromFile(getClass.getResource("/points.txt").getPath)
      .success
      .value shouldEqual NoContentResult

    db.readJs("SELECT * FROM test1")
      .success
      .value
      .queryResult
      .length shouldEqual 3
  }

  it should "write 2 points represented entities" in {

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

    db.writePoint(point1).success.value shouldEqual NoContentResult

    db.read[FakeEntity]("SELECT * FROM test2")
      .success
      .value
      .queryResult shouldEqual Array(FakeEntity("Martin", "Odersky", 54))

    db.bulkWritePoints(Array(point1, point2)).success.value shouldEqual NoContentResult

    db.read[FakeEntity]("SELECT * FROM test2")
      .success
      .value
      .queryResult shouldEqual Array(FakeEntity("Martin", "Odersky", 54), FakeEntity("Jame", "Franko", 36), FakeEntity("Martin", "Odersky", 54))
  }

  it should "retrieve multiple request" in {

    val multiQuery = db.bulkReadJs(
      Array(
        "SELECT * FROM test2",
        "SELECT * FROM test2 WHERE age < 40"
      )
    ).success.value

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
      .map(_.map(_.arrayValue.get.tail)) shouldEqual largeMultiJsonEntity.map(_.map(_.arrayValue.get.tail))
  }

  it should "write native" in {

    db.writeNative("test3,sex=Male,firstName=Jame,lastName=Lannister age=48").success.value shouldEqual NoContentResult

    db.read[FakeEntity]("SELECT * FROM test3")
      .success.value
      .queryResult shouldEqual Array(FakeEntity("Jame", "Lannister", 48))

    db.bulkWriteNative(Seq("test4,sex=Male,firstName=Jon,lastName=Snow age=24", "test4,sex=Female,firstName=Deny,lastName=Targaryen age=25")).success.value shouldEqual NoContentResult

    db.read[FakeEntity]("SELECT * FROM test4")
      .success.value
      .queryResult shouldEqual Array(FakeEntity("Female", "Deny", "Targaryen", 25), FakeEntity("Jon", "Snow", 24))
  }

  it should "return grouped result by sex and sum of ages" in {

    db
      .bulkWriteNative(Array("test5,sex=Male,firstName=Jon,lastName=Snow age=24", "test5,sex=Male,firstName=Rainer,lastName=Targaryen age=25"))
      .success.value shouldEqual NoContentResult

    db
      .readJs("SELECT SUM(\"age\") FROM \"test5\" GROUP BY \"sex\"")
      .success.value
      .groupedResult
      .map { case (k, v) => k.toSeq -> v } shouldEqual Array(Seq("Male") -> JArray(Array(JNum(0), JNum(49))))

  }

  it should "write escaped value" in {
    val p = Point("test6")
      .addTag("key,", "value,")
      .addField("field=key", 1)

    db.writePoint(p).success.value shouldEqual NoContentResult

    db.readJs("SELECT * FROM test6").success.value.queryResult.length shouldEqual 1

    mng.close() shouldEqual {}
    io.close() shouldEqual {}
  }
}
