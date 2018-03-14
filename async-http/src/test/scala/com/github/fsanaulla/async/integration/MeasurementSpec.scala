package com.github.fsanaulla.async.integration

import com.github.fsanaulla.async.utils.SampleEntitys._
import com.github.fsanaulla.async.utils.TestHelper.{FakeEntity, _}
import com.github.fsanaulla.chronicler.async.api.Measurement
import com.github.fsanaulla.chronicler.async.{InfluxAsyncHttpClient, InfluxDB}
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

  lazy val influx: InfluxAsyncHttpClient = InfluxDB.connect()

  lazy val meas: Measurement[FakeEntity] = influx.measurement[FakeEntity](safeDB, measName)
  lazy val db = influx.database(safeDB)

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
