package com.github.fsanaulla.chronicler.ahc.io.it

import java.nio.file.Paths

import com.github.fsanaulla.chronicler.ahc.io.InfluxIO
import com.github.fsanaulla.chronicler.ahc.management.InfluxMng
import com.github.fsanaulla.chronicler.ahc.shared.InfluxConfig
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

class CompressionSpec
  extends FlatSpec
  with Matchers
  with DockerizedInfluxDB
  with ScalaFutures
  with Eventually
  with IntegrationPatience {

  val testDB = "db"

  lazy val influxConf =
    InfluxConfig(host, port, Some(creds), gzipped = true)

  lazy val mng =
    InfluxMng(host, port, Some(creds), None)

  lazy val io =
    InfluxIO(influxConf)

  lazy val db = io.database(testDB)

  it should "ping database" in {
    eventually {
      io.ping.futureValue.right.get.version shouldEqual version
    }
  }

  it should "write data from file" in {
    mng.createDatabase(testDB).futureValue.right.get shouldEqual 200

    db.writeFromFile(Paths.get(getClass.getResource("/points.txt").getPath))
      .futureValue
      .right
      .get shouldEqual 204

    db.readJson("SELECT * FROM test1").futureValue.right.get.length shouldEqual 3
  }
}
