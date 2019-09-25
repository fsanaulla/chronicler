package com.github.fsanaulla.chronicler.urlhttp

import java.nio.file.Paths

import com.github.fsanaulla.chronicler.core.enums.Epochs
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.model.Point
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.urlhttp.SampleEntitys._
import com.github.fsanaulla.chronicler.urlhttp.io.{InfluxIO, UrlIOClient}
import com.github.fsanaulla.chronicler.urlhttp.management.{InfluxMng, UrlManagementClient}
import com.github.fsanaulla.chronicler.urlhttp.shared.InfluxConfig
import org.scalatest.{FlatSpec, Matchers}
import org.typelevel.jawn.ast.{JArray, JNum, JString}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 02.03.18
  */
class DatabaseApiSpec extends FlatSpec with Matchers with DockerizedInfluxDB {

  val testDB = "db"

  lazy val influxConf =
    InfluxConfig(host, port, credentials = Some(creds))

  lazy val mng: UrlManagementClient =
    InfluxMng(host, port, credentials = Some(creds))

  lazy val io: UrlIOClient =
    InfluxIO(influxConf)

  lazy val db: io.Database = io.database(testDB)

  it should "write data from file" in {
    mng.createDatabase(testDB).get.right.get shouldEqual 200

    db.writeFromFile(Paths.get(getClass.getResource("/points.txt").getPath))
      .get
      .right
      .get shouldEqual 204

    db.readJson("SELECT * FROM test1").get.right.get.length shouldEqual 3
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

    db.writePoint(point1).get.right.get shouldEqual 204

    db.readJson("SELECT * FROM test2", epoch = Epochs.Nanoseconds)
      .get
      .right
      .get
      // skip timestamp
      .map(jarr => jarr.copy(vs = jarr.vs.tail)) shouldEqual Array(
      JArray(Array(JNum(54), JString("Martin"), JString("Odersky"), JString("Male")))
    )

    db.bulkWritePoints(Array(point1, point2)).get.right.get shouldEqual 204

    db.readJson("SELECT * FROM test2", epoch = Epochs.Nanoseconds)
      .get
      .right
      .get
      // skip timestamp
      .map(jarr => jarr.copy(vs = jarr.vs.tail)) shouldEqual Array(
      JArray(Array(JNum(54), JString("Martin"), JString("Odersky"), JString("Male"))),
      JArray(Array(JNum(36), JString("Jame"), JString("Franko"), JString("Male"))),
      JArray(Array(JNum(54), JString("Martin"), JString("Odersky"), JString("Male")))
    )
  }

  it should "retrieve multiple request" in {
    val multiQuery = db
      .bulkReadJson(
        Array(
          "SELECT * FROM test2",
          "SELECT * FROM test2 WHERE age < 40"
        )
      )
      .get

    multiQuery.right.get.length shouldEqual 2
    multiQuery.right.get shouldBe a[Array[_]]

    multiQuery.right.get.head.length shouldEqual 3
    multiQuery.right.get.head shouldBe a[Array[_]]
    multiQuery.right.get.head.head shouldBe a[JArray]

    multiQuery.right.get.last.length shouldEqual 1
    multiQuery.right.get.last shouldBe a[Array[_]]
    multiQuery.right.get.last.head shouldBe a[JArray]

    multiQuery.right.get
      .map(_.map(_.arrayValue.right.get.tail)) shouldEqual largeMultiJsonEntity.map(
      _.map(_.arrayValue.right.get.tail)
    )
  }

  it should "write native" in {
    db.writeNative("test3,sex=Male,firstName=Jame,lastName=Lannister age=48")
      .get
      .right
      .get shouldEqual 204

    db.readJson("SELECT * FROM test3")
      .get
      .right
      .get
      .map(jarr => jarr.copy(vs = jarr.vs.tail)) shouldEqual Array(
      JArray(Array(JNum(48), JString("Jame"), JString("Lannister"), JString("Male")))
    )

    db.bulkWriteNative(
        Seq(
          "test4,sex=Male,firstName=Jon,lastName=Snow age=24",
          "test4,sex=Female,firstName=Deny,lastName=Targaryen age=25"
        )
      )
      .get
      .right
      .get shouldEqual 204

    db.readJson("SELECT * FROM test4")
      .get
      .right
      .get
      .map(jarr => jarr.copy(vs = jarr.vs.tail)) shouldEqual Array(
      JArray(Array(JNum(25), JString("Deny"), JString("Targaryen"), JString("Female"))),
      JArray(Array(JNum(24), JString("Jon"), JString("Snow"), JString("Male")))
    )
  }

  it should "return grouped result by sex and sum of ages" in {
    db.bulkWriteNative(
        Array(
          "test5,sex=Male,firstName=Jon,lastName=Snow age=24",
          "test5,sex=Male,firstName=Rainer,lastName=Targaryen age=25"
        )
      )
      .get
      .right
      .get shouldEqual 204

    db.readGroupedJson(
        "SELECT SUM(\"age\") FROM \"test5\" GROUP BY \"sex\"",
        epoch = Epochs.Nanoseconds
      )
      .get
      .right
      .get
      .map { case (k, v) => k.toSeq -> v } shouldEqual Array(
      Seq("Male") -> JArray(Array(JNum(0), JNum(49)))
    )
  }

  it should "write escaped value" in {
    val p = Point("test6")
      .addTag("key,", "value,")
      .addField("field=key", 1)

    db.writePoint(p).get.right.get shouldEqual 204

    db.readJson("SELECT * FROM test6").get.right.get.length shouldEqual 1
  }

  it should "validate empty response" in {
    db.readJson("SELECT * FROM test7").get.right.get.length shouldEqual 0

    mng.close() shouldEqual {}
    io.close() shouldEqual {}
  }
}
