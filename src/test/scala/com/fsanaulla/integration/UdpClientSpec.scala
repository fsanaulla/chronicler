package com.fsanaulla.integration

import com.fsanaulla.InfluxClientsFactory
import com.fsanaulla.api.Database
import com.fsanaulla.clients.{InfluxHttpClient, InfluxUdpClient}
import com.fsanaulla.utils.Synchronization._
import com.fsanaulla.utils.TestHelper._
import com.fsanaulla.utils.TestSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
class UdpClientSpec extends TestSpec {

  val influx: InfluxHttpClient = InfluxClientsFactory.createHttpClient(influxHost, username = Some("admin"), password = Some("admin"))
  val db: Database = influx.use("udp")
  implicit val timeout: FiniteDuration = 2 seconds

  "Udp influx client" should "correctly work" in {
    val udpInflux: InfluxUdpClient = InfluxClientsFactory.createUdpClient(influxHost)
    udpInflux.writeNative("meas1,firstName=Jame,lastName=Lannister age=48") shouldEqual {}
    udpInflux.close()

    Thread.sleep(1000)

    db.read[FakeEntity]("SELECT * FROM meas1").sync.queryResult shouldEqual Seq(FakeEntity("Jame", "Lannister", 48))

    influx.dropMeasurement("udp", "meas1").futureValue shouldEqual OkResult
    influx.close()
  }

}
