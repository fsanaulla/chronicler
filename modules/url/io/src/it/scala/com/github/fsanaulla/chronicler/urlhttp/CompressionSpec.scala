package com.github.fsanaulla.chronicler.urlhttp

import java.nio.file.Paths

import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.urlhttp.io.{InfluxIO, UrlIOClient}
import com.github.fsanaulla.chronicler.urlhttp.management.{InfluxMng, UrlManagementClient}
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{EitherValues, TryValues}

class CompressionSpec
    extends AnyFlatSpec
    with Matchers
    with ScalaFutures
    with EitherValues
    with TryValues
    with DockerizedInfluxDB
    with Eventually
    with IntegrationPatience {

  override def afterAll(): Unit = {
    mng.close()
    io.close()
    super.afterAll()
  }

  val testDB = "db"

  lazy val mng: UrlManagementClient =
    InfluxMng(s"http://$host", port, Some(creds))

  lazy val io: UrlIOClient =
    InfluxIO(s"http://$host", port, Some(creds), compress = true)

  lazy val db: io.Database = io.database(testDB)

  it should "ping database" in {
    eventually {
      io.ping.success.value.value.version shouldEqual version
    }
  }

  it should "write data from file" in {
    mng.createDatabase(testDB).success.value.value shouldEqual 200

    db.writeFromFile(Paths.get(getClass.getResource("/large_batch.txt").getPath))
      .success
      .value
      .value shouldEqual 204

    db.readJson("SELECT * FROM test1").success.value.value.length shouldEqual 10000
  }
}
