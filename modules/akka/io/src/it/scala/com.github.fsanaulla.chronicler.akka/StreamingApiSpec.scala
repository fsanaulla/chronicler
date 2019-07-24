package com.github.fsanaulla.chronicler.akka

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.StreamingApiSpec.Point
import com.github.fsanaulla.chronicler.akka.io.{
  AkkaDatabaseApi,
  AkkaIOClient,
  AkkaMeasurementApi,
  InfluxIO
}
import com.github.fsanaulla.chronicler.akka.management.InfluxMng
import com.github.fsanaulla.chronicler.akka.shared.InfluxConfig
import com.github.fsanaulla.chronicler.core.either
import com.github.fsanaulla.chronicler.core.enums.Epochs
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.macros.Influx
import com.github.fsanaulla.chronicler.macros.annotations.reader.epoch
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag, timestamp}
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import org.scalatest.{FlatSpecLike, Matchers}

import scala.concurrent.ExecutionContextExecutor

class StreamingApiSpec
  extends TestKit(ActorSystem())
  with FlatSpecLike
  with Futures
  with Matchers
  with DockerizedInfluxDB {

  val testDB   = "db"
  val measName = "test1"

  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val mat: ActorMaterializer       = ActorMaterializer()

  lazy val influxConf =
    InfluxConfig(host, port, credentials = Some(creds))

  lazy val mng =
    InfluxMng(influxConf)

  lazy val io: AkkaIOClient =
    InfluxIO(influxConf)

  lazy val db: AkkaDatabaseApi             = io.database(testDB)
  lazy val meas: AkkaMeasurementApi[Point] = io.measurement[Point](testDB, measName)

  it should "read chunked json result" in {
    mng.createDatabase(testDB).futureValue.right.get shouldEqual 200

    db.writeFromFile(Paths.get(getClass.getResource("/points.txt").getPath))
      .futureValue
      .right
      .get shouldEqual 204

    val src = db
      .readChunkedJson(s"SELECT * FROM $measName", chunkSize = 2)
      .futureValue

    val seq = src
      .runWith(Sink.seq)
      .futureValue

    val resSeq = either.seq(seq).right.get

    resSeq.size shouldEqual 2
    resSeq.flatMap(_.toSeq).size shouldEqual 3
  }

  it should "read typed chunked response" in {

    val src = meas
      .readChunked(
        s"SELECT * FROM $measName",
        epoch = Epochs.Milliseconds,
        chunkSize = 3
      )
      .futureValue
      .runWith(Sink.seq)
      .futureValue

    val resSeq = either.seq(src).right.get

    resSeq.size shouldEqual 1
    resSeq.flatMap(_.toSeq).size shouldEqual 3
  }

  it should "read chunked json one by one" in {
    val src = db
      .readChunkedJson(s"SELECT * FROM $measName", chunkSize = 1)
      .futureValue
      .runWith(Sink.seq)
      .futureValue

    val resSeq = either.seq(src).right.get

    resSeq.size shouldEqual 3
    resSeq.flatMap(_.toSeq).size shouldEqual 3
  }
}

object StreamingApiSpec {
  final case class Point(
      @tag direction: Option[String],
      @tag host: String,
      @tag region: Option[String],
      @field value: Double,
      @epoch @timestamp time: Long)

  implicit val rd: InfluxReader[Point] = Influx.reader
}
