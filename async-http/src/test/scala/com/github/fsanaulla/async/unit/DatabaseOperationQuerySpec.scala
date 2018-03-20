package com.github.fsanaulla.async.unit

import com.github.fsanaulla.async.utils.TestHelper._
import com.github.fsanaulla.chronicler.async.handlers.AsyncQueryHandler
import com.github.fsanaulla.core.query.DatabaseOperationQuery
import com.github.fsanaulla.core.test.utils.{EmptyCredentials, NonEmptyCredentials, TestSpec}
import com.github.fsanaulla.core.utils.constants.{Consistencys, Epochs, Precisions}
import com.softwaremill.sttp.Uri

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class DatabaseOperationQuerySpec extends TestSpec {

  trait Env extends AsyncQueryHandler with DatabaseOperationQuery[Uri] {
    val host = "localhost"
    val port = 8086
  }
  trait AuthEnv extends Env with NonEmptyCredentials
  trait NonAuthEnv extends Env with EmptyCredentials

  val testDB = "db"
  val testQuery = "SELECT * FROM test"


  "DatabaseOperationQuery" should "return correct write query" in new AuthEnv {

    writeToInfluxQuery(testDB, Consistencys.ONE, Precisions.NANOSECONDS, None).toString() shouldEqual queryTester(
      "/write",
      Map(
        "precision" -> "ns",
        "u" -> credentials.get.username,
        "consistency" -> "one",
        "db" -> testDB,
        "p" -> credentials.get.password
      )
    )

    writeToInfluxQuery(testDB, Consistencys.ALL, Precisions.NANOSECONDS, None).toString() shouldEqual queryTester(
      "/write",
      Map(
        "precision" -> "ns",
        "u" -> credentials.get.username,
        "consistency" -> "all",
        "db" -> testDB,
        "p" -> credentials.get.password)
    )
  }

  it should "return correct write query without auth " in new NonAuthEnv {
    writeToInfluxQuery(testDB, Consistencys.ONE, Precisions.NANOSECONDS, None).toString() shouldEqual
      queryTester("/write", Map("db" -> testDB, "precision" -> "ns", "consistency" -> "one"))

    writeToInfluxQuery(testDB, Consistencys.ONE, Precisions.MICROSECONDS, None).toString() shouldEqual
      queryTester("/write", Map("db" -> testDB, "precision" -> "u", "consistency" -> "one"))
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
    readFromInfluxSingleQuery(testDB, testQuery, Epochs.NANOSECONDS, pretty = false, chunked = false).toString() shouldEqual
      queryTester("/query", map)
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
    readFromInfluxBulkQuery(testDB, Seq("SELECT * FROM test", "SELECT * FROM test1"), Epochs.NANOSECONDS, pretty = false, chunked = false).toString() shouldEqual
      queryTester("/query", map)
  }
}
