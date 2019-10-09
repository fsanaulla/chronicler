package com.github.fsanaulla.chronicler.akka

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.io.{AkkaIOClient, InfluxIO}
import com.github.fsanaulla.chronicler.akka.management.{AkkaManagementClient, InfluxMng}
import com.github.fsanaulla.chronicler.akka.shared.InfluxConfig
import com.github.fsanaulla.chronicler.core.either
import com.github.fsanaulla.chronicler.core.either.EitherOps
import com.github.fsanaulla.chronicler.core.enums.Precisions
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import org.scalatest.{Matchers, WordSpecLike}
import org.typelevel.jawn.ast.{JArray, JNum, JString}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.Source

// https://github.com/fsanaulla/chronicler/issues/193
class GroupedApiSpec
  extends TestKit(ActorSystem())
  with WordSpecLike
  with Matchers
  with Futures
  with DockerizedInfluxDB {

  override def afterAll(): Unit = {
    mng.close()
    io.close()
    TestKit.shutdownActorSystem(system)
    super.afterAll()
  }

  implicit val ec: ExecutionContextExecutor = system.dispatcher
  val dbName                                = "mydb"

  lazy val influxConf =
    InfluxConfig(host, port, credentials = Some(creds), compress = false, None)
  lazy val mng: AkkaManagementClient =
    InfluxMng(host, port, credentials = Some(creds))
  lazy val io: AkkaIOClient =
    InfluxIO(influxConf)

  "Grouped Api" should {
    "prepare data for testing" in {
      (for {
        _ <- mng.createDatabase(dbName)

        db = io.database(dbName)

        data = Source
          .fromResource("h2feet_sample.txt")
          .getLines()
          .sliding(500, 500)

        wr <- Future
          .sequence(data.map(db.bulkWriteNative(_, precision = Precisions.Seconds)))
          .map(_.toSeq)
          .map(either.seq)

        count <- db.readJson("SELECT * FROM h2o_feet")
      } yield {
        wr.mapRight(_.forall(_ == 204)) shouldEqual Right(true)
        count.map(_.length > 0) shouldEqual Right(true)
      }).futureValue
    }

    "group by" should {
      lazy val db = io.database(dbName)

      "multiple tags" in {
        val sql = """
                    |select mean("water_level") 
                    |from h2o_feet 
                    |group by "location", time(10d) fill(none)
                    |""".stripMargin

        val groupedResult = db
          .readGroupedJson(sql)
          .futureValue
          .mapRight(_.map { case (tags, value) => tags.toList -> value.toList }.toList)

        groupedResult shouldEqual Right(
          List(
            (
              List("coyote_creek"),
              List(
                JArray(Array(JString("2015-08-13T00:00:00Z"), JNum(5.255175833333332))),
                JArray(Array(JString("2015-08-23T00:00:00Z"), JNum(5.282439166666671))),
                JArray(Array(JString("2015-09-02T00:00:00Z"), JNum(5.365102959566481))),
                JArray(Array(JString("2015-09-12T00:00:00Z"), JNum(5.543609345794391)))
              )
            ),
            (
              List("santa_monica"),
              List(
                JArray(Array(JString("2015-08-13T00:00:00Z"), JNum(3.3572483333333363))),
                JArray(Array(JString("2015-08-23T00:00:00Z"), JNum(3.374660833333338))),
                JArray(Array(JString("2015-09-02T00:00:00Z"), JNum(3.527653171953253))),
                JArray(Array(JString("2015-09-12T00:00:00Z"), JNum(3.887266586248496)))
              )
            )
          )
        )
      }

      "single tag" in {
        val sql = """
                    |select mean("water_level") 
                    |from h2o_feet 
                    |group by "location"
                    |""".stripMargin

        val groupedResult = db
          .readGroupedJson(sql)
          .futureValue
          .mapRight(_.map { case (tags, value) => tags.toList -> value.toList }.toList)

        groupedResult shouldEqual Right(
          List(
            (
              List("coyote_creek"),
              List(JArray(Array(JString("1970-01-01T00:00:00Z"), JNum(5.359342451341401))))
            ),
            (
              List("santa_monica"),
              List(JArray(Array(JString("1970-01-01T00:00:00Z"), JNum(3.530863470081006))))
            )
          )
        )
      }
    }
  }
}
