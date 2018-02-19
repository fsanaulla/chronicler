package com.github.fsanaulla.integration

import com.github.fsanaulla.api.Measurement
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.utils.SampleEntitys._
import com.github.fsanaulla.utils.TestHelper._
import com.github.fsanaulla.{InfluxAsyncHttpClient, InfluxClientFactory}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.09.17
  */
class MeasurementSpec extends TestSpec {

  val safeDB = "async_meas_db"
  val measName = "async_meas"

  lazy val influx: InfluxAsyncHttpClient = InfluxClientFactory.createHttpClient(
      host = influxHost,
      username = credentials.username,
      password = credentials.password
  )

  lazy val meas: Measurement[FakeEntity] = influx.measurement[FakeEntity](safeDB, measName)


  "Measurement[FakeEntity]" should "make single write" in {
    influx.createDatabase(safeDB).futureValue shouldEqual OkResult
    meas.write(singleEntity).futureValue shouldEqual NoContentResult
  }

  it should "make safe bulk write" in {
    meas.bulkWrite(multiEntitys).futureValue shouldEqual NoContentResult
  }

  it should "clean up everything" in {
    influx.dropMeasurement(safeDB, measName).futureValue shouldEqual OkResult
    influx.dropDatabase(safeDB).futureValue shouldEqual OkResult
    influx.close().futureValue shouldEqual {}
  }
}
