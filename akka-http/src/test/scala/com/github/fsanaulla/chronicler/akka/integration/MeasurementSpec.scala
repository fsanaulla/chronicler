package com.github.fsanaulla.chronicler.akka.integration

import com.github.fsanaulla.chronicler.akka.api.Measurement
import com.github.fsanaulla.chronicler.akka.utils.SampleEntitys._
import com.github.fsanaulla.chronicler.akka.utils.TestHelper.{FakeEntity, _}
import com.github.fsanaulla.chronicler.akka.{InfluxAkkaHttpClient, InfluxClientFactory}
import com.github.fsanaulla.core.test.utils.TestSpec

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.09.17
  */
class MeasurementSpec extends TestSpec {

  val safeDB = "akka_meas_db"

  val measName = "akka_meas"

  // INIT INFLUX CLIENT
  lazy val influx: InfluxAkkaHttpClient = InfluxClientFactory.createHttpClient(
      host = influxHost,
      username = credentials.username,
      password = credentials.password
  )

  lazy val meas: Measurement[FakeEntity] =
    influx.measurement[FakeEntity](safeDB, measName)


  "Safe entity" should "write typed entity" in {
    influx.createDatabase(safeDB).futureValue shouldEqual OkResult
    
    meas.write(singleEntity).futureValue shouldEqual NoContentResult
  }

  it should "make safe bulk write" in {
    meas.bulkWrite(multiEntitys).futureValue shouldEqual NoContentResult
  }
}
