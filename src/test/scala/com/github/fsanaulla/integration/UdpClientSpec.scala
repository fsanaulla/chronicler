package com.github.fsanaulla.integration

import com.github.fsanaulla.InfluxClientsFactory
import com.github.fsanaulla.api.AkkaDatabase
import com.github.fsanaulla.clients.{InfluxAkkaHttpClient, InfluxUdpClient}
import com.github.fsanaulla.model.Point
import com.github.fsanaulla.utils.Synchronization._
import com.github.fsanaulla.utils.TestHelper.{FakeEntity, _}
import com.github.fsanaulla.utils.TestSpec
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

  val influx: InfluxAkkaHttpClient = InfluxClientsFactory.createAkkaHttpClient(influxHost, username = Some("admin"), password = Some("admin"))

  val db: AkkaDatabase = influx.database("udp")

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
      "meas2,firstName=Jame,lastName=Lannister age=48",
      "meas2,firstName=Jon,lastName=Snow age=27")) shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.read[FakeEntity]("SELECT * FROM meas2").sync.queryResult shouldEqual Seq(FakeEntity("Jame", "Lannister", 48), FakeEntity("Jon", "Snow", 27))

    influx.dropMeasurement("udp", "meas2").futureValue shouldEqual OkResult
  }

  "Udp write from file" should "correctly work" in {
    val udpInflux: InfluxUdpClient = InfluxClientsFactory.createUdpClient(influxHost)
    udpInflux.writeFromFile("src/test/resources/points.txt") shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

//    db.readJs("SELECT * FROM test1").sync.queryResult.size shouldEqual 3

    influx.dropMeasurement("udp", "test1").futureValue shouldEqual OkResult
  }

  "Udp write typed" should "correctly work" in {
    val udpInflux: InfluxUdpClient = InfluxClientsFactory.createUdpClient(influxHost)
    udpInflux.write[FakeEntity]("meas4", FakeEntity("Name", "Surname", 10)) shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.read[FakeEntity]("SELECT * FROM meas4").sync.queryResult shouldEqual Seq(FakeEntity("Name", "Surname", 10))

    influx.dropMeasurement("udp", "meas4").futureValue shouldEqual OkResult
  }

  "Udp bulk write typed" should "correctly work" in {
    val udpInflux: InfluxUdpClient = InfluxClientsFactory.createUdpClient(influxHost)
    udpInflux.bulkWrite[FakeEntity]("meas5", Seq(FakeEntity("Name", "Surname", 10), FakeEntity("Name1", "Surname1", 11))) shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.read[FakeEntity]("SELECT * FROM meas5").sync.queryResult shouldEqual Seq(FakeEntity("Name", "Surname", 10), FakeEntity("Name1", "Surname1", 11))

    influx.dropMeasurement("udp", "meas5").futureValue shouldEqual OkResult
  }

  "Udp write point" should "correctly work" in {
    val udpInflux: InfluxUdpClient = InfluxClientsFactory.createUdpClient(influxHost)
    udpInflux.writePoint(
      Point("meas6")
        .addTag("firstName", "Jame")
        .addTag("lastName", "Lannister")
        .addField("age", 48)) shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000) // necessary for influx

    db.read[FakeEntity]("SELECT * FROM meas6").sync.queryResult shouldEqual Seq(FakeEntity("Jame", "Lannister", 48))

    influx.dropMeasurement("udp", "meas6").futureValue shouldEqual OkResult
  }

  "Udp write points" should "correctly work" in {
    val udpInflux: InfluxUdpClient = InfluxClientsFactory.createUdpClient(influxHost)
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

    db.read[FakeEntity]("SELECT * FROM meas7").sync.queryResult shouldEqual Seq(FakeEntity("Jame", "Lannister", 48), FakeEntity("Jon", "Snow", 27))

    influx.dropMeasurement("udp", "meas7").futureValue shouldEqual OkResult
  }

}
