package com.github.fsanaulla.chronicler.ahc.io.it

import java.nio.file.Paths

import com.github.fsanaulla.chronicler.ahc.io.{AhcIOClient, InfluxIO}
import com.github.fsanaulla.chronicler.ahc.management.{AhcManagementClient, InfluxMng}
import com.github.fsanaulla.chronicler.ahc.shared.InfluxConfig
import com.github.fsanaulla.chronicler.core.enums.Epochs
import com.github.fsanaulla.chronicler.core.model.Point
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import org.scalatest.{FlatSpec, Matchers}
import org.typelevel.jawn.ast.{JArray, JNum, JString, JValue}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 02.03.18
  */
class DatabaseApiSpec extends FlatSpec with Matchers with Futures with DockerizedInfluxDB {

  import DatabaseApiSpec._

  val testDB = "db"

  lazy val influxConf =
    InfluxConfig(host, port, credentials = Some(creds), compress = false, None)

  lazy val mng: AhcManagementClient =
    InfluxMng(host, port, credentials = Some(creds))

  lazy val io: AhcIOClient =
    InfluxIO(influxConf)

  lazy val db: io.Database = io.database(testDB)

  it should "write data from file" in {
    mng.createDatabase(testDB).futureValue.right.get shouldEqual 200

    db.writeFromFile(Paths.get(getClass.getResource("/points.txt").getPath))
      .futureValue
      .right
      .get shouldEqual 204

    db.readJson("SELECT * FROM test1").futureValue.right.get.length shouldEqual 3
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

    db.writePoint(point1).futureValue.right.get shouldEqual 204

    db.readJson("SELECT * FROM test2", epoch = Epochs.Nanoseconds)
      .futureValue
      .right
      .get
      // skip timestamp
      .map(jarr => jarr.copy(vs = jarr.vs.tail)) shouldEqual Array(
      JArray(Array(JNum(54), JString("Martin"), JString("Odersky"), JString("Male")))
    )

    db.bulkWritePoints(Array(point1, point2)).futureValue.right.get shouldEqual 204

    db.readJson("SELECT * FROM test2", epoch = Epochs.Nanoseconds)
      .futureValue
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
    db.readJson("SELECT * FROM test2").futureValue.right.get.length shouldEqual 3

    db.readJson("SELECT * FROM test2 WHERE age < 40").futureValue.right.get.length shouldEqual 1

    val multiQuery = db
      .bulkReadJson(Seq("SELECT * FROM test2", "SELECT * FROM test2 WHERE age < 40"))
      .futureValue
      .right
      .get

    multiQuery.length shouldEqual 2
    multiQuery shouldBe a[Array[_]]

    multiQuery.head.length shouldEqual 3
    multiQuery.head shouldBe a[Array[_]]
    multiQuery.head.head shouldBe a[JArray]

    multiQuery.last.length shouldEqual 1
    multiQuery.last shouldBe a[Array[_]]
    multiQuery.last.head shouldBe a[JArray]

    multiQuery
      .map(_.map(_.arrayValue.get.tail)) shouldEqual largeMultiJsonEntity.map(
      _.map(_.arrayValue.get.tail)
    )
  }

  it should "write native" in {
    db.writeNative("test3,sex=Male,firstName=Jame,lastName=Lannister age=48")
      .futureValue
      .right
      .get shouldEqual 204

    db.readJson("SELECT * FROM test3")
      .futureValue
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
      .futureValue
      .right
      .get shouldEqual 204

    db.readJson("SELECT * FROM test4")
      .futureValue
      .right
      .get
      .map(jarr => jarr.copy(vs = jarr.vs.tail)) shouldEqual Array(
      JArray(Array(JNum(25), JString("Deny"), JString("Targaryen"), JString("Female"))),
      JArray(Array(JNum(24), JString("Jon"), JString("Snow"), JString("Male")))
    )
  }

  it should "write escaped value" in {
    val p = Point("test6")
      .addTag("key,", "value,")
      .addField("field=key", 1)

    db.writePoint(p).futureValue.right.get shouldEqual 204

    db.readJson("SELECT * FROM test6").futureValue.right.get.length shouldEqual 1
  }

  it should "validate empty response" in {
    db.readJson("SELECT * FROM test7").futureValue.right.get.length shouldEqual 0

    mng.close() shouldEqual {}
    io.close() shouldEqual {}
  }
}

object DatabaseApiSpec {
  implicit final class JawnOps(private val jv: JValue) {

    def arrayValue: Option[Array[JValue]] = jv match {
      case JArray(arr) => Some(arr)
      case _           => None
    }
  }
}
