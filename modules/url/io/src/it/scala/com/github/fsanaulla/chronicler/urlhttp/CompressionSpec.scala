package com.github.fsanaulla.chronicler.urlhttp

import java.nio.file.Paths

import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.urlhttp.io.{InfluxIO, UrlIOClient}
import com.github.fsanaulla.chronicler.urlhttp.management.{InfluxMng, UrlManagementClient}
import com.github.fsanaulla.chronicler.urlhttp.shared.InfluxConfig
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.scalatest.{FlatSpec, Matchers}

class CompressionSpec
  extends FlatSpec
  with Matchers
  with DockerizedInfluxDB
  with Eventually
  with IntegrationPatience {

  val testDB = "db"

  lazy val influxConf =
    InfluxConfig(host, port, Some(creds), gzipped = true)

  lazy val mng: UrlManagementClient =
    InfluxMng(influxConf)

  lazy val io: UrlIOClient =
    InfluxIO(influxConf)

  lazy val db: io.Database = io.database(testDB)

  it should "ping database" in {
    eventually {
      io.ping.get.right.get.version shouldEqual version
    }
  }

  it should "write data from file" in {
    mng.createDatabase(testDB).get.right.get shouldEqual 200

    db.writeFromFile(Paths.get(getClass.getResource("/points.txt").getPath))
      .get
      .right
      .get shouldEqual 204

    db.readJson("SELECT * FROM test1").get.right.get.length shouldEqual 3
  }
}
