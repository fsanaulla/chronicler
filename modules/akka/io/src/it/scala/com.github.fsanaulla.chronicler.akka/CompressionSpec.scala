package com.github.fsanaulla.chronicler.akka

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.io.{AkkaDatabaseApi, InfluxIO}
import com.github.fsanaulla.chronicler.akka.management.InfluxMng
import com.github.fsanaulla.chronicler.akka.shared.InfluxConfig
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FlatSpecLike, Matchers}

import scala.concurrent.ExecutionContextExecutor

class CompressionSpec
  extends TestKit(ActorSystem())
  with FlatSpecLike
  with Matchers
  with DockerizedInfluxDB
  with ScalaFutures
  with IntegrationPatience {

  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val testDB = "db"

  lazy val influxConf =
    InfluxConfig(host, port, credentials = Some(creds), gzipped = true)

  lazy val mng =
    InfluxMng(host, port, credentials = Some(creds))

  lazy val io =
    InfluxIO(influxConf)

  lazy val db: AkkaDatabaseApi = io.database(testDB)

  it should "write data from file" in {
    mng.createDatabase(testDB).futureValue.right.get shouldEqual 200

    val wr = db
      .writeFromFile(Paths.get(getClass.getResource("/points.txt").getPath))
      .futureValue

    wr.right.get shouldEqual 204

    db.readJson("SELECT * FROM test1").futureValue.right.get.length shouldEqual 3
  }
}
