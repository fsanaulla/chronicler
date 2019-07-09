package com.github.fsanaulla.chronicler.urlhttp

import java.io.File

import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.urlhttp.io.{InfluxIO, UrlIOClient}
import com.github.fsanaulla.chronicler.urlhttp.management.{InfluxMng, UrlManagementClient}
import com.github.fsanaulla.chronicler.urlhttp.shared.InfluxConfig
import org.scalatest.{FlatSpec, Matchers}

class CompressionSpec extends FlatSpec with Matchers with DockerizedInfluxDB {
  val testDB = "db"

  lazy val influxConf =
    InfluxConfig(host, port, credentials = Some(creds), gzipped = true)

  lazy val mng: UrlManagementClient =
    InfluxMng(host, port, credentials = Some(creds))

  lazy val io: UrlIOClient =
    InfluxIO(influxConf)

  lazy val db: io.Database = io.database(testDB)

  it should "write data from file" in {
    mng.createDatabase(testDB).get.right.get shouldEqual 200

    val wr = db.writeFromFile(new File(getClass.getResource("/points.txt").getPath))
      .get

    println(wr)

    wr
      .right
      .get shouldEqual 204

    db.readJson("SELECT * FROM test1")
      .get
      .right
      .get
      .length shouldEqual 3
  }
}
