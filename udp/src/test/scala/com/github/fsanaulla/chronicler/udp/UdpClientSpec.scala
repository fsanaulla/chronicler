package com.github.fsanaulla.chronicler.udp

import com.github.fsanaulla.chronicler.async.{InfluxAsyncHttpClient, InfluxClientFactory}
import com.github.fsanaulla.core.model.Point
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{FlatSpec, Ignore, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 24.02.18
  */
@Ignore
class UdpClientSpec
  extends FlatSpec
    with Matchers
    with ScalaFutures
    with EmbeddedInfluxDB {

  implicit val pc: PatienceConfig = PatienceConfig(Span(20, Seconds), Span(1, Second))

  lazy val influxUdp = new InfluxUdpClient("localhost")

  lazy val influxHttp: InfluxAsyncHttpClient =
    InfluxClientFactory.createHttpClient("localhost")

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
