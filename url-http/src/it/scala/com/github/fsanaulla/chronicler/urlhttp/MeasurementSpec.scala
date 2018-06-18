package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.testing.it.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, FakeEntity}
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers
import com.github.fsanaulla.chronicler.urlhttp.SampleEntitys._
import com.github.fsanaulla.chronicler.urlhttp.api.Measurement
import org.scalatest.TryValues

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.09.17
  */
class MeasurementSpec extends FlatSpecWithMatchers with DockerizedInfluxDB with TryValues {

  val safeDB = "db"
  val measName = "meas"

  lazy val influx: InfluxUrlHttpClient =
    Influx.connect(host, port, Some(creds))

  lazy val meas: Measurement[FakeEntity] = influx.measurement[FakeEntity](safeDB, measName)

  "Measurement[FakeEntity]" should "make single write" in {
    influx.createDatabase(safeDB).success.value shouldEqual OkResult

    meas.write(singleEntity).success.value shouldEqual NoContentResult

    meas.read(s"SELECT * FROM $measName")
      .success.value
      .queryResult shouldEqual Array(singleEntity)
  }

  it should "make safe bulk write" in {
    meas.bulkWrite(multiEntitys).success.value shouldEqual NoContentResult

    meas.read(s"SELECT * FROM $measName")
      .success.value
      .queryResult
      .length shouldEqual 3

    influx.close() shouldEqual {}
  }
}
