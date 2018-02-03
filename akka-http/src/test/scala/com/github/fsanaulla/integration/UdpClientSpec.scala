package com.github.fsanaulla.integration

import com.github.fsanaulla.api.Database
import com.github.fsanaulla.{InfluxAkkaHttpClient, InfluxClientFactory, InfluxUdpClient, TestSpec}
import com.github.fsanaulla.core.model.Point
import com.github.fsanaulla.utils.TestHelper.{FakeEntity, _}

import scala.concurrent.duration._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
class UdpClientSpec extends TestSpec {

  val influx: InfluxAkkaHttpClient = InfluxClientFactory.createHttpClient(influxHost, username = Some("admin"), password = Some("admin"))

  val db: Database = influx.database("udp")

  implicit val timeout: FiniteDuration = 5 seconds

  "Udp single native write" should "correctly work" in {
    val udpInflux: InfluxUdpClient = new InfluxUdpClient(influxHost)
    udpInflux.writeNative("meas1,firstName=Jame,lastName=Lannister age=48") shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.read[FakeEntity]("SELECT * FROM meas1").futureValue.queryResult shouldEqual Seq(FakeEntity("Jame", "Lannister", 48))

    influx.dropMeasurement("udp", "meas1").futureValue shouldEqual OkResult
  }

  "Udp bulk native write" should "correctly work" in {
    val udpInflux: InfluxUdpClient = new InfluxUdpClient(influxHost)
    udpInflux.bulkWriteNative(Seq(
      "meas2,firstName=Jame,lastName=Lannister age=48",
      "meas2,firstName=Jon,lastName=Snow age=27")) shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.read[FakeEntity]("SELECT * FROM meas2").futureValue.queryResult shouldEqual Seq(FakeEntity("Jame", "Lannister", 48), FakeEntity("Jon", "Snow", 27))

    influx.dropMeasurement("udp", "meas2").futureValue shouldEqual OkResult
  }

  "Udp write from file" should "correctly work" in {
    val udpInflux: InfluxUdpClient = new InfluxUdpClient(influxHost)
    udpInflux.writeFromFile("src/test/resources/points.txt") shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

//    db.readJs("SELECT * FROM test1").sync.queryResult.size shouldEqual 3

    influx.dropMeasurement("udp", "test1").futureValue shouldEqual OkResult
  }

  "Udp write typed" should "correctly work" in {
    val udpInflux: InfluxUdpClient = new InfluxUdpClient(influxHost)
    udpInflux.write[FakeEntity]("meas4", FakeEntity("Name", "Surname", 10)) shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.read[FakeEntity]("SELECT * FROM meas4").futureValue.queryResult shouldEqual Seq(FakeEntity("Name", "Surname", 10))

    influx.dropMeasurement("udp", "meas4").futureValue shouldEqual OkResult
  }

  "Udp bulk write typed" should "correctly work" in {
    val udpInflux: InfluxUdpClient = new InfluxUdpClient(influxHost)
    udpInflux.bulkWrite[FakeEntity]("meas5", Seq(FakeEntity("Name", "Surname", 10), FakeEntity("Name1", "Surname1", 11))) shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.read[FakeEntity]("SELECT * FROM meas5").futureValue.queryResult shouldEqual Seq(FakeEntity("Name", "Surname", 10), FakeEntity("Name1", "Surname1", 11))

    influx.dropMeasurement("udp", "meas5").futureValue shouldEqual OkResult
  }

  "Udp write point" should "correctly work" in {
    val udpInflux: InfluxUdpClient = new InfluxUdpClient(influxHost)
    udpInflux.writePoint(
      Point("meas6")
        .addTag("firstName", "Jame")
        .addTag("lastName", "Lannister")
        .addField("age", 48)) shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.read[FakeEntity]("SELECT * FROM meas6").futureValue.queryResult shouldEqual Seq(FakeEntity("Jame", "Lannister", 48))

    influx.dropMeasurement("udp", "meas6").futureValue shouldEqual OkResult
  }

  "Udp write points" should "correctly work" in {
    val udpInflux: InfluxUdpClient = new InfluxUdpClient(influxHost)
    udpInflux.bulkWritePoints(
      Seq(
        Point("meas7")
          .addTag("firstName", "Jame")
          .addTag("lastName", "Lannister")
          .addField("age", 48),
        Point("meas7")
          .addTag("firstName", "Jon")
          .addTag("lastName", "Snow")
          .addField("age", 27))) shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.read[FakeEntity]("SELECT * FROM meas7").futureValue.queryResult shouldEqual Seq(FakeEntity("Jame", "Lannister", 48), FakeEntity("Jon", "Snow", 27))

    influx.dropMeasurement("udp", "meas7").futureValue shouldEqual OkResult
  }

}
