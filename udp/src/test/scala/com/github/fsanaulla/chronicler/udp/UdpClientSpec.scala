package com.github.fsanaulla.chronicler.udp

import com.github.fsanaulla.chronicler.async.api.Database
import com.github.fsanaulla.chronicler.async.{InfluxAsyncHttpClient, InfluxDB}
import com.github.fsanaulla.core.model.{InfluxFormatter, Point}
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.macros.Macros
import com.github.fsanaulla.macros.annotations.{field, tag}
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 24.02.18
  */
class UdpClientSpec extends TestSpec with EmbeddedInfluxDB {
  import UdpClientSpec._

  override def udpPort: Option[Int] = Some(8089)

  lazy val influxUdp: InfluxUDPClient = InfluxUDP.connect()

  lazy val influxHttp: InfluxAsyncHttpClient = InfluxDB.connect()

  lazy val udp: Database = influxHttp.database("udp")

  "Udp client" should "write" in {

    val t = Test("f", 1)

    influxUdp.write[Test]("cpu", t).futureValue shouldEqual {}

    Thread.sleep(3000)

    udp
      .read[Test]("SELECT * FROM cpu")
      .futureValue
      .queryResult shouldEqual Seq(t)
  }

  it should "write point" in {
    val p = Point("cpu")
      .addTag("name", "d")
      .addField("age", 2)

    influxUdp.writePoint(p).futureValue shouldEqual {}

    Thread.sleep(3000)

    udp
      .read[Test]("SELECT * FROM cpu")
      .futureValue
      .queryResult.size shouldEqual 2
  }

  it should "write native" in {
    influxUdp.writeNative("cpu,name=v age=3")

    Thread.sleep(3000)

    udp
      .read[Test]("SELECT * FROM cpu")
      .futureValue
      .queryResult.size shouldEqual 3
  }
}

object UdpClientSpec {
  case class Test(@tag name: String, @field age: Int)
  implicit val fmt: InfluxFormatter[Test] = Macros.format[Test]
}
