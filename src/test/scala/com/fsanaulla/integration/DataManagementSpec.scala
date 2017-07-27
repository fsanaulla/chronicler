package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.model.{DatabaseInfo, MeasurementInfo}
import com.fsanaulla.utils.Helper._
import com.fsanaulla.utils.SampleEntitys.{multiEntitys, singleEntity}
import com.whisk.docker.impl.spotify.DockerKitSpotify
import com.whisk.docker.scalatest.DockerTestKit
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 26.07.17
  */
class DataManagementSpec
  extends FlatSpec
    with Matchers
    with DockerTestKit
    with DockerKitSpotify
    with DockerInfluxService {

  implicit val pc = PatienceConfig(Span(20, Seconds), Span(1, Second))
  final val dbName = "mydb"

  "Influx container" should "get up and run correctly" in {
    // CHECKING CONTAINER
    isContainerReady(influxdbContainer).futureValue shouldBe true
    influxdbContainer.getPorts().futureValue.get(8086) should not be None
    influxdbContainer.getIpAddresses().futureValue should not be Seq.empty
  }

  "Data management operation" should "correctly work" in {
    val influx = InfluxClient(influxdbContainer.getIpAddresses().futureValue.head)

    influx.createDatabase(dbName).futureValue.status shouldEqual OK
    influx.showDatabases().futureValue shouldEqual Seq(DatabaseInfo("mydb"))

    val db = influx.use(dbName)
    db.bulkWrite("meas1", multiEntitys).futureValue.status shouldEqual NoContent
    db.read("SELECT * FROM meas1").futureValue shouldEqual multiEntitys

    db.write("meas2", singleEntity).futureValue.status shouldEqual NoContent
    db.read("SELECT * FROM meas2").futureValue shouldEqual Seq(singleEntity)

    influx.showMeasurement(dbName).futureValue shouldEqual Seq(MeasurementInfo("meas1"), MeasurementInfo("meas2"))

    influx.dropMeasurement(dbName, "meas1").futureValue.status shouldEqual OK
    db.read("SELECT * FROM meas1").futureValue shouldEqual Nil
    influx.showMeasurement(dbName).futureValue shouldEqual Seq(MeasurementInfo("meas2"))

    influx.dropDatabase(dbName).futureValue.status shouldEqual OK
    influx.showDatabases().futureValue shouldEqual Nil
  }
 }
