package com.github.fsanaulla.chronicler.async

import com.github.fsanaulla.chronicler.async.SampleEntitys._
import com.github.fsanaulla.chronicler.async.api.Measurement
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, FakeEntity, Futures}
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.09.17
  */
class MeasurementSpec extends FlatSpecWithMatchers with Futures with DockerizedInfluxDB {

  val safeDB = "db"
  val measName = "meas"

  lazy val influx: InfluxAsyncHttpClient =
    Influx(host, port, Some(creds))

  lazy val meas: Measurement[FakeEntity] = influx.measurement[FakeEntity](safeDB, measName)

  "Measurement[FakeEntity]" should "make single write" in {
    influx.createDatabase(safeDB).futureValue shouldEqual OkResult

    meas.write(singleEntity).futureValue shouldEqual NoContentResult

    meas.read(s"SELECT * FROM $measName")
      .futureValue
      .queryResult shouldEqual Array(singleEntity)
  }

  it should "make safe bulk write" in {
    meas.bulkWrite(multiEntitys).futureValue shouldEqual NoContentResult

    meas.read(s"SELECT * FROM $measName")
      .futureValue
      .queryResult
      .length shouldEqual 3

    influx.close() shouldEqual {}
  }
}
