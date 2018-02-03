package com.github.fsanaulla.unit

import akka.http.scaladsl.model.Uri
import com.github.fsanaulla.TestSpec
import com.github.fsanaulla.core.query.DatabaseOperationQuery
import com.github.fsanaulla.core.utils.constants.{Consistencys, Epochs, Precisions}
import com.github.fsanaulla.handlers.AkkaQueryHandler
import com.github.fsanaulla.utils.TestHelper._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class DatabaseOperationQuerySpec
  extends TestSpec
    with AkkaQueryHandler
    with DatabaseOperationQuery[Uri] {

  val testDB = "db"
  val testQuery = "SELECT * FROM test"

  "DatabaseOperationQuery" should "return correct write query" in {
    writeToInfluxQuery(testDB, Consistencys.ONE, Precisions.NANOSECONDS, None) shouldEqual
      writeTester(s"precision=ns&u=${credentials.username.get}&consistency=one&db=$testDB&p=${credentials.password.get}")

    writeToInfluxQuery(testDB, Consistencys.ONE, Precisions.NANOSECONDS, None)(emptyCredentials) shouldEqual
      writeTester(s"db=$testDB&precision=ns&consistency=one")

    writeToInfluxQuery(testDB, Consistencys.ALL, Precisions.NANOSECONDS, None) shouldEqual
      writeTester(s"precision=ns&u=${credentials.username.get}&consistency=all&db=$testDB&p=${credentials.password.get}")

    writeToInfluxQuery(testDB, Consistencys.ONE, Precisions.MICROSECONDS, None)(emptyCredentials) shouldEqual
      writeTester(s"db=$testDB&precision=u&consistency=one")
  }

  it should "return correct single read query" in {
    val map = Map[String, String](
      "db" -> testDB,
      "u" -> credentials.username.get,
      "p" -> credentials.password.get,
      "pretty" -> "false",
      "chunked" -> "false",
      "epoch" -> "ns",
      "q" -> "SELECT * FROM test"
    )
    readFromInfluxSingleQuery(testDB, testQuery, Epochs.NANOSECONDS, pretty = false, chunked = false) shouldEqual queryTesterSimple(map)
  }

  it should "return correct bulk read query" in {
    val map = Map[String, String](
      "db" -> testDB,
      "u" -> credentials.username.get,
      "p" -> credentials.password.get,
      "pretty" -> "false",
      "chunked" -> "false",
      "epoch" -> "ns",
      "q" -> "SELECT * FROM test;SELECT * FROM test1"
    )
    readFromInfluxBulkQuery(testDB, Seq("SELECT * FROM test", "SELECT * FROM test1"), Epochs.NANOSECONDS, pretty = false, chunked = false) shouldEqual queryTesterSimple(map)
  }
}
