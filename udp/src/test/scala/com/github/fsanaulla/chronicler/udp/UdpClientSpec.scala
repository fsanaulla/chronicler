package com.github.fsanaulla.chronicler.udp

import com.github.fsanaulla.core.model.Point
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 24.02.18
  */
class UdpClientSpec extends TestSpec with EmbeddedInfluxDB {
  override def udpPort: Option[Int] = Some(8089)

  lazy val influxUdp = InfluxDB.connect()

  lazy val influxHttp = com.github.fsanaulla.chronicler.async.InfluxDB.connect()

  "Udp client" should "write to InfluxDB" in {

    val p = Point("test_meas")
      .addTag("tag1", "fs")
      .addField("field1", 45)

    influxUdp.writePoint(p).futureValue shouldEqual {}

    Thread.sleep(3000)

    influxHttp
      .database("udp")
      .readJs("SELECT * FROM test_meas")
      .futureValue
      .queryResult should not equal Nil
  }

}
