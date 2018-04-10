package com.github.fsanaulla.chronicler.udp

import java.io.File

import com.github.fsanaulla.chronicler.async.api.Database
import com.github.fsanaulla.chronicler.async.{InfluxAsyncHttpClient, InfluxDB}
import com.github.fsanaulla.core.model.{InfluxFormatter, Point}
import com.github.fsanaulla.core.test.TestSpec
import com.github.fsanaulla.core.testing.configurations.InfluxUDPConf
import com.github.fsanaulla.macros.Macros
import com.github.fsanaulla.macros.annotations.{field, tag}
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 24.02.18
  */
class UdpClientSpec extends TestSpec with EmbeddedInfluxDB with InfluxUDPConf {
  import UdpClientSpec._
  
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
      .queryResult shouldEqual Array(t)
  }

  it should "bulk write" in {
    val t = Test("f", 1)
    val t1 = Test("g", 2)

    influxUdp.bulkWrite[Test]("cpu1", t :: t1 :: Nil).futureValue shouldEqual {}

    Thread.sleep(3000)

    udp
      .read[Test]("SELECT * FROM cpu1")
      .futureValue
      .queryResult shouldEqual Array(t, t1)
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
      .queryResult
      .length shouldEqual 2
  }

  it should "bulk write point" in {
    val p = Point("cpu2")
      .addTag("name", "d")
      .addField("age", 2)

    val p1 = Point("cpu2")
      .addTag("name", "e")
      .addField("age", 3)

    influxUdp.bulkWritePoints(p :: p1 :: Nil).futureValue shouldEqual {}

    Thread.sleep(3000)

    udp
      .read[Test]("SELECT * FROM cpu2")
      .futureValue
      .queryResult shouldEqual Array(Test("d", 2), Test("e", 3))
  }

  it should "write native" in {
    influxUdp.writeNative("cpu,name=v age=3")

    Thread.sleep(3000)

    udp
      .read[Test]("SELECT * FROM cpu")
      .futureValue
      .queryResult
      .length shouldEqual 3
  }

  it should "bulk write native" in {
    influxUdp.bulkWriteNative("cpu3,name=v age=3" :: "cpu3,name=b age=5" :: Nil)

    Thread.sleep(3000)

    udp
      .read[Test]("SELECT * FROM cpu3")
      .futureValue
      .queryResult shouldEqual Array(Test("b", 5), Test("v", 3))
  }

  it should "write from file" in {
    influxUdp.writeFromFile(new File(getClass.getResource("/points.txt").getPath))
      .futureValue shouldEqual {}

    udp.readJs("SELECT * FROM test1")
      .futureValue
      .queryResult
      .length shouldEqual 3
  }

}

object UdpClientSpec {
  case class Test(@tag name: String, @field age: Int)
  implicit val fmt: InfluxFormatter[Test] = Macros.format[Test]
}
