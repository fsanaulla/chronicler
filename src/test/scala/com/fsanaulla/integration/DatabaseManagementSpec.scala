package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.model.{Field, TagValue}
import com.fsanaulla.utils.SampleEntitys.{multiEntitys, singleEntity}
import com.fsanaulla.utils.TestHelper.{FakeEntity, NoContentResult, OkResult}
import com.fsanaulla.utils.TestSpec

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 26.07.17
  */
class DatabaseManagementSpec extends TestSpec {

  final val dbName = "data_db"

  "Data management operation" should "correctly work" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClient(host = influxHost, username = credentials.username, password = credentials.password)
    influx.createDatabase(dbName).futureValue shouldEqual OkResult

    influx.showDatabases().futureValue.queryResult.contains(dbName) shouldEqual true

    val db = influx.use(dbName)

    db.bulkWrite[FakeEntity]("meas1", multiEntitys).futureValue shouldEqual NoContentResult
    db.read[FakeEntity]("SELECT * FROM meas1").futureValue.queryResult shouldEqual multiEntitys

    influx.showFieldKeys(dbName, "meas1").futureValue.queryResult shouldEqual Seq(Field("age", "float"))

    influx.showTagKeys(dbName, "meas1").futureValue.queryResult shouldEqual Seq("firstName", "lastName")

    influx.showTagValues(dbName, "meas1", Seq("firstName")).futureValue.queryResult shouldEqual Seq(TagValue("firstName", "Harold"), TagValue("firstName", "Harry"))

    db.write[FakeEntity]("meas2", singleEntity).futureValue shouldEqual NoContentResult
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
