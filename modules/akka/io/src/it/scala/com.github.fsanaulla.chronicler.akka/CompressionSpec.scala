package com.github.fsanaulla.chronicler.akka

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.io.{AkkaDatabaseApi, AkkaIOClient, InfluxIO}
import com.github.fsanaulla.chronicler.akka.management.{AkkaManagementClient, InfluxMng}
import com.github.fsanaulla.chronicler.akka.shared.InfluxConfig
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import org.scalatest.EitherValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContextExecutor

class CompressionSpec
    extends TestKit(ActorSystem())
    with AnyFlatSpecLike
    with Matchers
    with DockerizedInfluxDB
    with ScalaFutures
    with EitherValues
    with IntegrationPatience {

  implicit val ec: ExecutionContextExecutor = system.dispatcher

  override def afterAll(): Unit = {
    mng.close()
    io.close()
    TestKit.shutdownActorSystem(system)
    super.afterAll()
  }

  val testDB = "db"

  lazy val influxConf: InfluxConfig =
    InfluxConfig(host, port, credentials = Some(creds), compress = true)

  lazy val mng: AkkaManagementClient =
    InfluxMng(host, port, credentials = Some(creds))

  lazy val io: AkkaIOClient =
    InfluxIO(influxConf)

  lazy val db: AkkaDatabaseApi = io.database(testDB)

  it should "write data from file" in {
    mng.createDatabase(testDB).futureValue.value shouldEqual 200

    db.writeFromFile(Paths.get(getClass.getResource("/large_batch.txt").getPath))
      .futureValue
      .value shouldEqual 204

    db.readJson("SELECT * FROM test1").futureValue.value.length shouldEqual 10000
  }
}
