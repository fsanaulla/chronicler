package com.github.fsanaulla.chronicler.urlhttp.integration

import com.github.fsanaulla.chronicler.testing.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.{DockerizedInfluxDB, TestSpec}
import com.github.fsanaulla.chronicler.urlhttp.api.Measurement
import com.github.fsanaulla.chronicler.urlhttp.utils.SampleEntitys._
import com.github.fsanaulla.chronicler.urlhttp.utils.TestHelper.FakeEntity
import com.github.fsanaulla.chronicler.urlhttp.{Influx, InfluxUrlHttpClient}
import org.scalatest.TryValues

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.09.17
  */
class MeasurementSpec extends TestSpec with DockerizedInfluxDB with TryValues {

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
      .result shouldEqual Array(singleEntity)
  }

  it should "make safe bulk write" in {
    meas.bulkWrite(multiEntitys).success.value shouldEqual NoContentResult

    meas.read(s"SELECT * FROM $measName")
      .success.value
      .result
      .length shouldEqual 3

    influx.close() shouldEqual {}
  }
}
