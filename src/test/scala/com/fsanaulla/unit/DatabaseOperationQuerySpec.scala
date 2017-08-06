package com.fsanaulla.unit

import com.fsanaulla.query.DatabaseOperationQuery
import com.fsanaulla.utils.TestHelper._
import com.fsanaulla.utils.constants.{Consistencys, Precisions}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class DatabaseOperationQuerySpec
  extends FlatSpec
    with Matchers
    with DatabaseOperationQuery {

  val testDB = "db"
  val optTestUsername = Some("User")
  val optTestPass = Some("pass")
  val testQuery = "SELECT * FROM test"

  "write query generator" should "return correct query" in {
    writeToInfluxQuery(testDB, optTestUsername, optTestPass) shouldEqual
      writeTester(s"precision=ns&u=${optTestUsername.get}&consistency=one&db=$testDB&p=${optTestPass.get}")

    writeToInfluxQuery(testDB, optTestUsername) shouldEqual
      writeTester(s"db=$testDB&precision=ns&consistency=one")

    writeToInfluxQuery(testDB, optTestUsername, optTestPass, consistency = Consistencys.ALL) shouldEqual
      writeTester(s"precision=ns&u=${optTestUsername.get}&consistency=all&db=$testDB&p=${optTestPass.get}")

    writeToInfluxQuery(testDB, precision = Precisions.MICROSECONDS) shouldEqual
      writeTester(s"db=$testDB&precision=u&consistency=one")
  }

  "read single query generator" should "return correct query" in {
    val map = Map[String, String](
      "db" -> testDB,
      "u" -> optTestUsername.get,
      "p" -> optTestPass.get,
      "pretty" -> "false",
      "chunked" -> "false",
      "epoch" -> "ns",
      "q" -> "SELECT * FROM test"
    )
    readFromInfluxSingleQuery(testDB, testQuery, optTestUsername, optTestPass) shouldEqual queryTesterSimple(map)
  }

  "read bulk query generator" should "return correct query" in {
    val map = Map[String, String](
      "db" -> testDB,
      "u" -> optTestUsername.get,
      "p" -> optTestPass.get,
      "pretty" -> "false",
      "chunked" -> "false",
      "epoch" -> "ns",
      "q" -> "SELECT * FROM test;SELECT * FROM test1"
    )
    readFromInfluxBulkQuery(testDB, Seq("SELECT * FROM test", "SELECT * FROM test1"), optTestUsername, optTestPass) shouldEqual queryTesterSimple(map)
  }
}
