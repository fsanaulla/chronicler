package com.github.fsanaulla.chronicler.udp

import java.io.File

import com.github.fsanaulla.chronicler.core.model.{InfluxFormatter, Point}
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers
import com.github.fsanaulla.chronicler.urlhttp.io.{InfluxIO, UrlIOClient}
import jawn.ast.{JArray, JNum, JString}
import org.scalatest.TryValues
import org.testcontainers.containers.InfluxDBContainer

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.comØ
  * Date: 24.02.18
  */
// todo: move it to test-containers
class UdpClientSpec
  extends FlatSpecWithMatchers
    with DockerizedInfluxDB
    with TryValues {

  import UdpClientSpec._

  override def beforeStartContainer(container: InfluxDBContainer[Nothing]): InfluxDBContainer[Nothing] = {
    container.withExposedPorts(8086, 8089)
    container.withEnv("INFLUXDB_UDP_ENABLED", "true")
    container.withEnv("INFLUXDB_UDP_BIND_ADDRESS", ":8089")
    container.withEnv("INFLUXDB_UDP_DATABASE", "udp")
    container
  }



  it should "write" in {
    val influxUdp: InfluxUDPClient = InfluxUdp(host, mappedPort(8089))
    val influxHttp: UrlIOClient = InfluxIO(host, mappedPort(8086), Some(creds))
    val meas = influxHttp.measurement[Test]("udp", "cpu")

    val t = Test("f", 1)

    for (_ <- 0 to 1000) {
      influxUdp.write[Test]("cpu", t) shouldEqual {}
    }

    Thread.sleep(10000)

    val res = meas
      .read("SELECT * FROM cpu")

    res
      .success
      .value
      .queryResult shouldEqual Array(t)
  }

//  it should "bulk write" in {
//    val t = Test("f", 1)
//    val t1 = Test("g", 2)
//
//    influxUdp.bulkWrite[Test]("cpu1", t :: t1 :: Nil) shouldEqual {}
//
//    Thread.sleep(3000)
//
//    udpDb
//      .read[Test]("SELECT * FROM cpu1")
//      .success
//      .value
//      .queryResult shouldEqual Array(t, t1)
//  }
//
//
//  it should "write point" in {
//    val p = Point("cpu")
//      .addTag("name", "d")
//      .addField("age", 2)
//
//    influxUdp.writePoint(p) shouldEqual {}
//
//    Thread.sleep(3000)
//
//    udpDb
//      .read[Test]("SELECT * FROM cpu")
//      .success
//      .value
//      .queryResult
//      .length shouldEqual 2
//  }
//
//  it should "bulk write point" in {
//    val p = Point("cpu2")
//      .addTag("name", "d")
//      .addField("age", 2)
//
//    val p1 = Point("cpu2")
//      .addTag("name", "e")
//      .addField("age", 3)
//
//    influxUdp.bulkWritePoints(p :: p1 :: Nil) shouldEqual {}
//
//    Thread.sleep(3000)
//
//    udpDb
//      .read[Test]("SELECT * FROM cpu2")
//      .success
//      .value
//      .queryResult shouldEqual Array(Test("d", 2), Test("e", 3))
//  }
//
//  it should "write native" in {
//    influxUdp.writeNative("cpu,name=v age=3")
//
//    Thread.sleep(3000)
//
//    udpDb
//      .read[Test]("SELECT * FROM cpu")
//      .success
//      .value
//      .queryResult
//      .length shouldEqual 3
//  }
//
//  it should "bulk write native" in {
//    influxUdp.bulkWriteNative("cpu3,name=v age=3" :: "cpu3,name=b age=5" :: Nil)
//
//    Thread.sleep(3000)
//
//    udpDb
//      .read[Test]("SELECT * FROM cpu3")
//      .success
//      .value
//      .queryResult shouldEqual Array(Test("b", 5), Test("v", 3))
//  }
//
//  it should "write from file" in {
//    influxUdp.writeFromFile(new File(getClass.getResource("/points.txt").getPath)) shouldEqual {}
//
//    udpDb.readJs("SELECT * FROM test1")
//      .success
//      .value
//      .queryResult
//      .length shouldEqual 3
//  }
}

object UdpClientSpec {
  import com.github.fsanaulla.chronicler.core.jawn._

  case class Test(name: String, age: Int)

  implicit val fmt: InfluxFormatter[Test] = new InfluxFormatter[Test] {
    override def read(js: JArray): Test = js.vs.tail match {
      case Array(age: JNum, name: JString) => Test(name, age)
    }

    override def write(obj: Test): String =
      s"name=${obj.name} age=${obj.age}"
  }
}
