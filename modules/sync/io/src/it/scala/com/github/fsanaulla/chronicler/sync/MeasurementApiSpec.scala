package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.sync.shared.InfluxConfig
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, FakeEntity}
import com.github.fsanaulla.chronicler.testing.BaseSpec
import com.github.fsanaulla.chronicler.urlhttp.SampleEntitys._
import com.github.fsanaulla.chronicler.sync.io.{InfluxIO, SyncIOClient}
import com.github.fsanaulla.chronicler.sync.management.{InfluxMng, SyncManagementClient}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.{EitherValues, TryValues}
import org.scalatest.BeforeAndAfterAll

/** Created by Author: fayaz.sanaulla@gmail.com Date: 28.09.17
  */
class MeasurementApiSpec
    extends BaseSpec
    with ScalaFutures
    with EitherValues
    with TryValues
    with DockerizedInfluxDB
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    mng.close()
    io.close()
    super.afterAll()
  }

  val db       = "db"
  val measName = "meas"

  lazy val influxConf = InfluxConfig(host, port, Some(credentials))
  lazy val mng        = InfluxMng(influxConf)
  lazy val io         = InfluxIO(influxConf)

  lazy val meas: io.Measurement[FakeEntity] =
    io.measurement[FakeEntity](db, measName)

  "Measurement API" - {

    "should" - {

      "write" - {

        "single point" in {
          mng.createDatabase(db).success.value.value shouldEqual 200
          meas.write(singleEntity).success.value.value shouldEqual 204
          meas.read(s"SELECT * FROM $measName").success.value.value shouldEqual Seq(singleEntity)
        }

        "multiple points" in {
          meas.bulkWrite(multiEntitys).success.value.value shouldEqual 204
          meas.read(s"SELECT * FROM $measName").success.value.value.length shouldEqual 3
        }
      }
    }
  }
}
