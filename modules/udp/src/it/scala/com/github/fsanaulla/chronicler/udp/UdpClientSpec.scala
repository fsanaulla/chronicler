package com.github.fsanaulla.chronicler.udp

import java.io.File

import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.model.{InfluxReader, InfluxWriter, Point}
import com.github.fsanaulla.chronicler.urlhttp.io.{InfluxIO, UrlIOClient}
import com.github.fsanaulla.chronicler.urlhttp.management.{InfluxMng, UrlManagementClient}
import org.scalatest._
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.typelevel.jawn.ast.{JArray, JNum, JString}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.comØ
  * Date: 24.02.18
  */
class UdpClientSpec
  extends FlatSpec
  with Matchers
  with TryValues
  with Eventually
  with IntegrationPatience
  with BeforeAndAfterAll {

  import UdpClientSpec._

  val service     = "influxdb"
  val servicePort = 8086

  lazy val container: DockerComposeContainer[Nothing] = {
    val cont = new DockerComposeContainer(
      new File(getClass.getResource("/docker-compose.yml").getPath)
    )

    cont.withLocalCompose(true)
    cont.withExposedService(service, 8086, Wait.forHttp("/ping").forStatusCode(204))

    cont
  }

  override def beforeAll(): Unit = {
    container.start()
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    influxHttpIO.close()
    influxHttpMng.close()
    influxUdp.close()
    container.stop()
    super.afterAll()
  }

  val udpPort = 8089

  lazy val host: String      = container.getServiceHost(service, servicePort)
  lazy val httpPort: Integer = container.getServicePort(service, servicePort)

  lazy val influxUdp: InfluxUDPClient           = InfluxUdp(host, udpPort)
  lazy val influxHttpIO: UrlIOClient            = InfluxIO(host, httpPort /*, Some(creds)*/ )
  lazy val influxHttpMng: UrlManagementClient   = InfluxMng(host, httpPort /*, Some(creds)*/ )
  lazy val meas: influxHttpIO.Measurement[Test] = influxHttpIO.measurement[Test]("udp", "cpu")

  it should "write" in {
    val t = Test("f", 1)

    influxUdp.write[Test]("cpu", t).success.value shouldEqual {}

    eventually {
      meas
        .read("SELECT * FROM cpu")
        .get
        .right
        .get shouldEqual Array(t)
    }
  }

  it should "bulk write" in {
    val t  = Test("f", 1)
    val t1 = Test("g", 2)

    influxUdp.bulkWrite[Test]("cpu1", t :: t1 :: Nil).success.value shouldEqual {}

    eventually {
      meas
        .read("SELECT * FROM cpu1")
        .get
        .right
        .get shouldEqual Array(t, t1)
    }
  }

  it should "write point" in {
    val p = Point("cpu2")
      .addTag("name", "d")
      .addField("age", 2)

    influxUdp.writePoint(p).success.value shouldEqual {}

    eventually {
      meas
        .read("SELECT * FROM cpu2")
        .get
        .right
        .get shouldEqual Array(Test("d", 2))
    }
  }

  it should "bulk write point" in {
    val p = Point("cpu3")
      .addTag("name", "d")
      .addField("age", 2)

    val p1 = Point("cpu3")
      .addTag("name", "e")
      .addField("age", 3)

    influxUdp.bulkWritePoints(p :: p1 :: Nil).success.value shouldEqual {}

    eventually {
      meas
        .read("SELECT * FROM cpu3")
        .get
        .right
        .get shouldEqual Array(Test("d", 2), Test("e", 3))
    }
  }

  it should "write native" in {
    influxUdp.writeNative("cpu4,name=v age=3").success.value shouldEqual {}

    eventually {
      meas
        .read("SELECT * FROM cpu4")
        .get
        .right
        .get shouldEqual Array(Test("v", 3))
    }
  }

  it should "bulk write native" in {
    influxUdp
      .bulkWriteNative("cpu5,name=v age=3" :: "cpu5,name=b age=5" :: Nil)
      .success
      .value shouldEqual {}

    eventually {
      meas
        .read("SELECT * FROM cpu5")
        .get
        .right
        .get shouldEqual Array(Test("b", 5), Test("v", 3))
    }
  }

  it should "write from file" in {
    val db = influxHttpIO.database("udp")
    influxUdp
      .writeFromFile(new File(getClass.getResource("/points.txt").getPath))
      .success
      .value shouldEqual {}

    eventually {
      db.readJson("SELECT * FROM test1").get.right.get.length shouldEqual 3
    }
  }
}

object UdpClientSpec {
  import com.github.fsanaulla.chronicler.core.jawn._

  case class Test(name: String, age: Int)

  implicit val rd: InfluxReader[Test] = new InfluxReader[Test] {
    override def read(js: JArray): ErrorOr[Test] = js.vs.tail match {
      case Array(age: JNum, name: JString) => Right(Test(name, age))
      case _                               => Left(new Error(""))
    }
    override def readUnsafe(js: JArray): Test = read(js).right.get
  }

  implicit val wr: InfluxWriter[Test] = new InfluxWriter[Test] {
    override def write(obj: Test): ErrorOr[String] =
      Right(s"name=${obj.name} age=${obj.age}")
  }
}
