package com.github.fsanaulla.chronicler.udp

import java.io.File

import com.github.fsanaulla.chronicler.core.model.{InfluxFormatter, Point}
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers
import com.github.fsanaulla.chronicler.urlhttp.InfluxUrlHttpClient
import com.github.fsanaulla.chronicler.urlhttp.api.Database
import com.github.fsanaulla.core.testing.configurations.InfluxUDPConf
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB
import jawn.ast.{JArray, JNum, JString}
import org.scalatest.TryValues

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 24.02.18
  */
// todo: move it to test-containers
class UdpClientSpec
  extends FlatSpecWithMatchers
    with EmbeddedInfluxDB
    with InfluxUDPConf
    with TryValues {
  import UdpClientSpec._

  val host = "localhost"
  lazy val influxUdp: InfluxUDPClient =
    com.github.fsanaulla.chronicler.udp.Influx(host)

  lazy val influxHttp: InfluxUrlHttpClient =
    com.github.fsanaulla.chronicler.urlhttp.Influx(host)

  lazy val udp: Database = influxHttp.database("udp")

  "Udp client" should "write" in {

    val t = Test("f", 1)

    influxUdp.write[Test]("cpu", t) shouldEqual {}

    Thread.sleep(3000)

    udp
      .read[Test]("SELECT * FROM cpu")
      .success
      .value
      .queryResult shouldEqual Array(t)
  }

  it should "bulk write" in {
    val t = Test("f", 1)
    val t1 = Test("g", 2)

    influxUdp.bulkWrite[Test]("cpu1", t :: t1 :: Nil) shouldEqual {}

    Thread.sleep(3000)

    udp
      .read[Test]("SELECT * FROM cpu1")
      .success
      .value
      .queryResult shouldEqual Array(t, t1)
  }


  it should "write point" in {
    val p = Point("cpu")
      .addTag("name", "d")
      .addField("age", 2)

    influxUdp.writePoint(p) shouldEqual {}

    Thread.sleep(3000)

    udp
      .read[Test]("SELECT * FROM cpu")
      .success
      .value
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

    influxUdp.bulkWritePoints(p :: p1 :: Nil) shouldEqual {}

    Thread.sleep(3000)

    udp
      .read[Test]("SELECT * FROM cpu2")
      .success
      .value
      .queryResult shouldEqual Array(Test("d", 2), Test("e", 3))
  }

  it should "write native" in {
    influxUdp.writeNative("cpu,name=v age=3")

    Thread.sleep(3000)

    udp
      .read[Test]("SELECT * FROM cpu")
      .success
      .value
      .queryResult
      .length shouldEqual 3
  }

  it should "bulk write native" in {
    influxUdp.bulkWriteNative("cpu3,name=v age=3" :: "cpu3,name=b age=5" :: Nil)

    Thread.sleep(3000)

    udp
      .read[Test]("SELECT * FROM cpu3")
      .success
      .value
      .queryResult shouldEqual Array(Test("b", 5), Test("v", 3))
  }

  it should "write from file" in {
    influxUdp.writeFromFile(new File(getClass.getResource("/points.txt").getPath)) shouldEqual {}

    udp.readJs("SELECT * FROM test1")
      .success
      .value
      .queryResult
      .length shouldEqual 3
  }
}

object UdpClientSpec {
  import com.github.fsanaulla.chronicler.core.utils.PrimitiveJawnImplicits._

  case class Test(name: String, age: Int)

  implicit val fmt: InfluxFormatter[Test] = new InfluxFormatter[Test] {
    override def read(js: JArray): Test = js.vs.tail match {
      case Array(age: JNum, name: JString) => Test(name, age)
    }

    override def write(obj: Test): String =
      s"name=${obj.name} age=${obj.age}"
  }
}
