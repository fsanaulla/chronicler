package com.github.fsanaulla.chronicler.akka.query

import _root_.akka.http.scaladsl.model.Uri
import com.github.fsanaulla.chronicler.akka.TestHelper._
import com.github.fsanaulla.chronicler.akka.handlers.AkkaQueryHandler
import com.github.fsanaulla.chronicler.core.enums.{Consistencies, Epochs, Precisions}
import com.github.fsanaulla.chronicler.core.model.HasCredentials
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import com.github.fsanaulla.chronicler.testing.unit.{EmptyCredentials, FlatSpecWithMatchers, NonEmptyCredentials}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class DatabaseOperationQuerySpec extends FlatSpecWithMatchers {

  trait Env extends AkkaQueryHandler with DatabaseOperationQuery[Uri] { self: HasCredentials =>
    val host = "localhost"
    val port = 8086
  }
  trait AuthEnv extends Env with NonEmptyCredentials
  trait NonAuthEnv extends Env with EmptyCredentials

  val testDB = "db"
  val testQuery = "SELECT * FROM test"


  "DatabaseOperationQuery" should "return correct write query" in new AuthEnv {

    writeToInfluxQuery(testDB, Consistencies.ONE, Precisions.NANOSECONDS, None) shouldEqual writeTester(
      Map(
        "precision" -> "ns",
        "u" -> credentials.get.username,
        "consistency" -> "one",
        "db" -> testDB,
        "p" -> credentials.get.password
      )
    )

    writeToInfluxQuery(testDB, Consistencies.ALL, Precisions.NANOSECONDS, None) shouldEqual writeTester(
      Map(
        "precision" -> "ns",
        "u" -> credentials.get.username,
        "consistency" -> "all",
        "db" -> testDB,
        "p" -> credentials.get.password)
    )
  }

  it should "return correct write query without auth " in new NonAuthEnv {
    writeToInfluxQuery(testDB, Consistencies.ONE, Precisions.NANOSECONDS, None) shouldEqual
      writeTester(Map("db" -> testDB, "precision" -> "ns", "consistency" -> "one"))

    writeToInfluxQuery(testDB, Consistencies.ONE, Precisions.MICROSECONDS, None) shouldEqual
      writeTester(Map("db" -> testDB, "precision" -> "u", "consistency" -> "one"))
  }

  it should "return correct single read query" in new AuthEnv {
    val map = Map[String, String](
      "db" -> testDB,
      "u" -> credentials.get.username,
      "p" -> credentials.get.password,
      "pretty" -> "false",
      "chunked" -> "false",
      "epoch" -> "ns",
      "q" -> "SELECT * FROM test"
    )
    readFromInfluxSingleQuery(testDB, testQuery, Epochs.NANOSECONDS, pretty = false, chunked = false) shouldEqual queryTesterSimple(map)
  }

  it should "return correct bulk read query" in new AuthEnv {
    val map = Map[String, String](
      "db" -> testDB,
      "u" -> credentials.get.username,
      "p" -> credentials.get.password,
      "pretty" -> "false",
      "chunked" -> "false",
      "epoch" -> "ns",
      "q" -> "SELECT * FROM test;SELECT * FROM test1"
    )
    readFromInfluxBulkQuery(testDB, Seq("SELECT * FROM test", "SELECT * FROM test1"), Epochs.NANOSECONDS, pretty = false, chunked = false) shouldEqual queryTesterSimple(map)
  }
}
