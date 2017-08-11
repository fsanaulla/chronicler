package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.model._
import com.fsanaulla.utils.Extension._
import com.fsanaulla.utils.SampleEntitys.{multiEntitys, singleEntity}
import com.fsanaulla.utils.TestHelper.{FakeEntity, NoContentResult, OkResult}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 26.07.17
  */
class DataManagementSpec extends IntegrationSpec {

  final val dbName = "data_db"

  "Data management operation" should "correctly work" in {
    val influx = InfluxClient(host)

    influx.createDatabase(dbName).sync shouldEqual OkResult

    influx.showDatabases().sync.queryResult.contains(DatabaseInfo(dbName)) shouldEqual true

    val db = influx.use(dbName)
    db.bulkWrite("meas1", multiEntitys).sync shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM meas1").sync.queryResult shouldEqual multiEntitys

    db.write("meas2", singleEntity).sync shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM meas2").sync.queryResult shouldEqual Seq(singleEntity)

    influx.showMeasurement(dbName).sync.queryResult shouldEqual Seq(MeasurementInfo("meas1"), MeasurementInfo("meas2"))

    influx.dropMeasurement(dbName, "meas1").sync shouldEqual OkResult

    db.read[FakeEntity]("SELECT * FROM meas1").sync.queryResult shouldEqual Nil

    influx.showMeasurement(dbName).sync.queryResult shouldEqual Seq(MeasurementInfo("meas2"))

    influx.dropDatabase(dbName).sync shouldEqual OkResult

    influx.showDatabases().sync.queryResult.contains(DatabaseInfo(dbName)) shouldEqual false

    influx.close()
  }
 }
