package com.fsanaulla.integration

import com.fsanaulla.InfluxClientsFactory
import com.fsanaulla.api.Database
import com.fsanaulla.clients.{InfluxHttpClient, InfluxUdpClient}
import com.fsanaulla.model.Point
import com.fsanaulla.utils.Synchronization._
import com.fsanaulla.utils.TestHelper._
import com.fsanaulla.utils.TestSpec
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
class UdpClientSpec extends TestSpec with BeforeAndAfterAll {

  override def afterAll(): Unit = influx.close()

  val influx: InfluxHttpClient = InfluxClientsFactory.createHttpClient(influxHost, username = Some("admin"), password = Some("admin"))

  val db: Database = influx.use("udp")

  implicit val timeout: FiniteDuration = 5 seconds

  "Udp single native write" should "correctly work" in {
    val udpInflux: InfluxUdpClient = InfluxClientsFactory.createUdpClient(influxHost)
    udpInflux.writeNative("meas1,firstName=Jame,lastName=Lannister age=48") shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.read[FakeEntity]("SELECT * FROM meas1").sync.queryResult shouldEqual Seq(FakeEntity("Jame", "Lannister", 48))

    influx.dropMeasurement("udp", "meas1").futureValue shouldEqual OkResult
  }

  "Udp bulk native write" should "correctly work" in {
    val udpInflux: InfluxUdpClient = InfluxClientsFactory.createUdpClient(influxHost)
    udpInflux.bulkWriteNative(Seq(
      "meas1,firstName=Jame,lastName=Lannister age=48",
      "meas1,firstName=Jon,lastName=Snow age=27")) shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.read[FakeEntity]("SELECT * FROM meas1").sync.queryResult shouldEqual Seq(FakeEntity("Jame", "Lannister", 48), FakeEntity("Jon", "Snow", 27))

    influx.dropMeasurement("udp", "meas1").futureValue shouldEqual OkResult
  }

  "Udp write from file" should "correctly work" in {
    val udpInflux: InfluxUdpClient = InfluxClientsFactory.createUdpClient(influxHost)
    udpInflux.writeFromFile("src/test/resources/points.txt") shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.readJs("SELECT * FROM test1").sync.queryResult.size shouldEqual 3

    influx.dropMeasurement("udp", "test1").futureValue shouldEqual OkResult
  }

  "Udp write typed" should "correctly work" in {
    val udpInflux: InfluxUdpClient = InfluxClientsFactory.createUdpClient(influxHost)
    udpInflux.write[FakeEntity]("meas1", FakeEntity("Name", "Surname", 10)) shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.read[FakeEntity]("SELECT * FROM meas1").sync.queryResult shouldEqual Seq(FakeEntity("Name", "Surname", 10))

    influx.dropMeasurement("udp", "meas1").futureValue shouldEqual OkResult
  }

  "Udp bulk write typed" should "correctly work" in {
    val udpInflux: InfluxUdpClient = InfluxClientsFactory.createUdpClient(influxHost)
    udpInflux.bulkWrite[FakeEntity]("meas1", Seq(FakeEntity("Name", "Surname", 10), FakeEntity("Name1", "Surname1", 11))) shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.read[FakeEntity]("SELECT * FROM meas1").sync.queryResult shouldEqual Seq(FakeEntity("Name", "Surname", 10), FakeEntity("Name1", "Surname1", 11))

    influx.dropMeasurement("udp", "meas1").futureValue shouldEqual OkResult
  }

  "Udp write point" should "correctly work" in {
    val udpInflux: InfluxUdpClient = InfluxClientsFactory.createUdpClient(influxHost)
    udpInflux.writePoint(
      Point("meas1")
        .addTag("firstName", "Jame")
        .addTag("lastName", "Lannister")
        .addField("age", 48)) shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.read[FakeEntity]("SELECT * FROM meas1").sync.queryResult shouldEqual Seq(FakeEntity("Jame", "Lannister", 48))

    influx.dropMeasurement("udp", "meas1").futureValue shouldEqual OkResult
  }

  "Udp write points" should "correctly work" in {
    val udpInflux: InfluxUdpClient = InfluxClientsFactory.createUdpClient(influxHost)
    udpInflux.bulkWritePoints(
      Seq(
        Point("meas1")
          .addTag("firstName", "Jame")
          .addTag("lastName", "Lannister")
          .addField("age", 48),
        Point("meas1")
          .addTag("firstName", "Jon")
          .addTag("lastName", "Snow")
          .addField("age", 27))) shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.read[FakeEntity]("SELECT * FROM meas1").sync.queryResult shouldEqual Seq(FakeEntity("Jame", "Lannister", 48), FakeEntity("Jon", "Snow", 27))

    influx.dropMeasurement("udp", "meas1").futureValue shouldEqual OkResult
  }

}
