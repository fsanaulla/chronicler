package com.github.fsanaulla.chronicler.urlhttp

import java.nio.file.Paths
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.urlhttp.io.{InfluxIO, SyncIOClient}
import com.github.fsanaulla.chronicler.urlhttp.management.{InfluxMng, UrlManagementClient}
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.{EitherValues, TryValues}
import com.github.fsanaulla.chronicler.testing.BaseSpec
import org.scalatest.BeforeAndAfterAll

class CompressionSpec
    extends BaseSpec
    with ScalaFutures
    with EitherValues
    with TryValues
    with DockerizedInfluxDB
    with Eventually
    with IntegrationPatience
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    mng.close()
    io.close()
    super.afterAll()
  }

  val testDB = "db"

  lazy val mng: UrlManagementClient =
    InfluxMng(host, port, Some(credentials))

  lazy val io: SyncIOClient =
    InfluxIO(host, port, Some(credentials), compress = true)

  lazy val db: io.Database = io.database(testDB)

  "Compression functionality" - {

    "should" - {

      "ping database" in {
        io.ping.success.value.value.version shouldEqual version
      }

      "write data from file and read it back" - {

        "create database" in {
          mng.createDatabase(testDB).success.value.value shouldEqual 200
        }

        "write data from file" in {
          db.writeFromFile(Paths.get(getClass.getResource("/large_batch.txt").getPath))
            .success
            .value
            .value shouldEqual 204
        }

        "read it back" in {
          db.readJson("SELECT * FROM test1").success.value.value.length shouldEqual 10000
        }
      }
    }
  }
}
