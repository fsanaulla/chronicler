package com.github.fsanaulla.chronicler.akka

import _root_.akka.actor.ActorSystem
import _root_.akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.SampleEntitys._
import com.github.fsanaulla.chronicler.akka.io.api.Measurement
import com.github.fsanaulla.chronicler.akka.io.{AkkaIOClient, InfluxIO}
import com.github.fsanaulla.chronicler.akka.management.AkkaManagementClient
import com.github.fsanaulla.chronicler.core.model.InfluxConfig
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, FakeEntity, Futures}
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.09.17
  */
class MeasurementApiSpec
  extends TestKit(ActorSystem())
    with FlatSpecWithMatchers
    with Futures
    with DockerizedInfluxDB {

  val safeDB = "db"
  val measName = "meas"

  lazy val influxConf = InfluxConfig(host, port, credentials = Some(creds), gzipped = false)

  lazy val mng: AkkaManagementClient =
    management.InfluxMng.management(influxConf)

  lazy val io: AkkaIOClient = InfluxIO(influxConf)
  lazy val meas: Measurement[FakeEntity] =
    io.measurement[FakeEntity](safeDB, measName)

  "Measurement[FakeEntity]" should "write" in {
    mng.createDatabase(safeDB).futureValue shouldEqual OkResult

    meas.write(singleEntity).futureValue shouldEqual NoContentResult

    meas.read(s"SELECT * FROM $measName")
      .futureValue
      .queryResult shouldEqual Seq(singleEntity)
  }

  it should "bulk write" in {
    meas.bulkWrite(multiEntitys).futureValue shouldEqual NoContentResult

    meas.read(s"SELECT * FROM $measName")
      .futureValue
      .queryResult
      .length shouldEqual 3

    mng.close() shouldEqual {}
    io.close() shouldEqual {}
  }
}
