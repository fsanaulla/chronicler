package com.github.fsanaulla.chronicler.ahc.io.it

import java.nio.file.Paths

import com.github.fsanaulla.chronicler.async.io.{InfluxIO}
import com.github.fsanaulla.chronicler.async.management.{InfluxMng}
import com.github.fsanaulla.chronicler.core.alias.Id
import com.github.fsanaulla.chronicler.core.api.DatabaseApi
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import org.asynchttpclient.Response
import org.scalatest.{EitherValues, BeforeAndAfterAll}
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.github.fsanaulla.chronicler.async.io.InfluxIO
import com.github.fsanaulla.chronicler.async.management.InfluxMng

class CompressionSpec
    extends AnyFlatSpec
    with Matchers
    with EitherValues
    with DockerizedInfluxDB
    with ScalaFutures
    with Eventually
    with IntegrationPatience
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    mng.close()
    io.close()
    super.afterAll()
  }

  val testDB = "db"

  lazy val mng =
    InfluxMng(host, port, Some(credentials), None)

  lazy val io =
    InfluxIO(host, port, Some(credentials), compress = true)

  lazy val db = io.database(testDB)

  it should "ping database" in {
    eventually {
      io.ping.futureValue.value.version shouldEqual version
    }
  }

  it should "write data from file" in {
    mng.createDatabase(testDB).futureValue.value shouldEqual 200

    db.writeFromFile(Paths.get(getClass.getResource("/large_batch.txt").getPath))
      .futureValue
      .value shouldEqual 204

    db.readJson("SELECT * FROM test1").futureValue.value.length shouldEqual 10000
  }
}
