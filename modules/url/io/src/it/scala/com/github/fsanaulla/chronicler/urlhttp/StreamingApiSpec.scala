package com.github.fsanaulla.chronicler.urlhttp

import java.io.File

import com.github.fsanaulla.chronicler.core.enums.Epochs
import com.github.fsanaulla.chronicler.macros.annotations.reader.epoch
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag, timestamp}
import com.github.fsanaulla.chronicler.macros.auto._
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.urlhttp.StreamingApiSpec.Point
import com.github.fsanaulla.chronicler.urlhttp.io.{InfluxIO, UrlDatabaseApi, UrlIOClient, UrlMeasurementApi}
import com.github.fsanaulla.chronicler.urlhttp.management.{InfluxMng, UrlManagementClient}
import com.github.fsanaulla.chronicler.urlhttp.shared.InfluxConfig
import jawn.ast.JValue
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable.ArrayBuffer

class StreamingApiSpec extends FlatSpec with Matchers with DockerizedInfluxDB {

  val testDB = "db"
  val measName = "test1"
  lazy val influxConf =
    InfluxConfig(host, port, credentials = Some(creds))

  lazy val mng: UrlManagementClient =
    InfluxMng(influxConf)

  lazy val io: UrlIOClient =
    InfluxIO(influxConf)

  lazy val db: UrlDatabaseApi = io.database(testDB)
  lazy val meas: UrlMeasurementApi[Point] = io.measurement[Point](testDB, measName)

  it should "read chunked json result" in {
    mng.createDatabase(testDB).get.right.get shouldEqual 200

    db.writeFromFile(new File(getClass.getResource("/points.txt").getPath))
      .get
      .right
      .get shouldEqual 204

    val arr = new ArrayBuffer[JValue](3)

    val iter = db.readChunkedJson(s"SELECT * FROM $measName", chunkSize = 2)

    while (iter.hasNext) {
      val nxt = iter.next()

      nxt match {
        case Right(vl) => vl.foreach(arr += _)
        case _ => fail()
      }
    }

    arr.size shouldEqual 3
  }

  it should "read typed chunked response" in {
    val arr = new ArrayBuffer[Point](3)

    val iter = meas.readChunked(s"SELECT * FROM $measName", epoch = Epochs.Milliseconds, chunkSize = 2)

    while (iter.hasNext) {
      val nxt = iter.next()

      nxt match {
        case Right(vl) =>
          vl.foreach(arr += _)
        case Left(err) => fail(err)
      }
    }

    arr.size shouldEqual 3
  }

  it should "read chunked json as one batch" in {
    val arr = new ArrayBuffer[JValue](3)

    val iter = db.readChunkedJson(s"SELECT * FROM $measName")

    val nxt = iter.next()
    nxt match {
      case Right(vl) => vl.foreach(arr += _)
      case _ => fail()
    }

    arr.size shouldEqual 3
    iter.hasNext shouldEqual false
  }
}

object StreamingApiSpec {
  case class Point(@tag direction: Option[String],
                   @tag host: String,
                   @tag region: Option[String],
                   @field value: Double,
                   @epoch @timestamp time: Long)
}
