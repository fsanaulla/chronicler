package com.github.fsanaulla.chronicler.akka.integration

import com.github.fsanaulla.chronicler.akka.api.{Database, Measurement}
import com.github.fsanaulla.chronicler.akka.utils.SampleEntitys._
import com.github.fsanaulla.chronicler.akka.utils.TestHelper.{FakeEntity, _}
import com.github.fsanaulla.chronicler.akka.{InfluxAkkaHttpClient, InfluxDB}
import com.github.fsanaulla.core.test.utils.ResultMatchers._
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.09.17
  */
class MeasurementSpec extends TestSpec with EmbeddedInfluxDB {

  val safeDB = "db"
  val measName = "meas"

  lazy val influx: InfluxAkkaHttpClient = InfluxDB.connect()

  lazy val meas: Measurement[FakeEntity] = influx.measurement[FakeEntity](safeDB, measName)
  lazy val db: Database = influx.database(safeDB)

  "Measurement[FakeEntity]" should "make single write" in {
    influx.createDatabase(safeDB).futureValue shouldEqual OkResult

    meas.write(singleEntity).futureValue shouldEqual NoContentResult

    db.readJs(s"SELECT * FROM $measName")
      .futureValue
      .queryResult should not equal Nil
  }

  it should "make safe bulk write" in {
    meas.bulkWrite(multiEntitys).futureValue shouldEqual NoContentResult

    db.readJs(s"SELECT * FROM $measName")
      .futureValue
      .queryResult
      .size should be > 1

    influx.close() shouldEqual {}
  }
}
