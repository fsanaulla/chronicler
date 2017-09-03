package com.github.fsanaulla.integration

import com.github.fsanaulla.InfluxClientsFactory
import com.github.fsanaulla.model.{FieldInfo, TagValue}
import com.github.fsanaulla.utils.SampleEntitys.{multiEntitys, singleEntity}
import com.github.fsanaulla.utils.TestHelper.{FakeEntity, NoContentResult, OkResult}
import com.github.fsanaulla.utils.TestSpec

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 26.07.17
  */
class DatabaseManagementSpec extends TestSpec {

  final val dbName = "data_management_spec_db"

  "Data management operation" should "correctly work" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClientsFactory.createHttpClient(host = influxHost, username = credentials.username, password = credentials.password)
    influx.createDatabase(dbName).futureValue shouldEqual OkResult

    influx.showDatabases().futureValue.queryResult.contains(dbName) shouldEqual true

    val db = influx.use(dbName)
    val meas1 = db.measurement[FakeEntity]("meas1")
    val meas2 = db.measurement[FakeEntity]("meas2")

    meas1.bulkWrite(multiEntitys).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM meas1").futureValue.queryResult shouldEqual multiEntitys

    influx.showFieldKeys(dbName, "meas1").futureValue.queryResult shouldEqual Seq(FieldInfo("age", "float"))

    influx.showTagKeys(dbName, "meas1").futureValue.queryResult shouldEqual Seq("firstName", "lastName")

    influx.showTagValues(dbName, "meas1", Seq("firstName")).futureValue.queryResult shouldEqual Seq(TagValue("firstName", "Harold"), TagValue("firstName", "Harry"))

    meas2.write(singleEntity).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM meas2").futureValue.queryResult shouldEqual Seq(singleEntity)

    influx.showMeasurement(dbName).futureValue.queryResult shouldEqual Seq("meas1", "meas2")

    influx.dropMeasurement(dbName, "meas1").futureValue shouldEqual OkResult

    db.read[FakeEntity]("SELECT * FROM meas1").futureValue.queryResult shouldEqual Nil

    influx.showMeasurement(dbName).futureValue.queryResult shouldEqual Seq("meas2")

    influx.dropDatabase(dbName).futureValue shouldEqual OkResult

    influx.showDatabases().futureValue.queryResult.contains(dbName) shouldEqual false

    influx.close()
  }
 }
