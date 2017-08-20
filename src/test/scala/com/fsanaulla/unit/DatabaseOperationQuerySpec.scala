package com.fsanaulla.unit

import com.fsanaulla.query.DatabaseOperationQuery
import com.fsanaulla.utils.TestCredentials
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
    with DatabaseOperationQuery
    with TestCredentials {

  val testDB = "db"
  val testQuery = "SELECT * FROM test"

  "write query generator" should "return correct query" in {
    writeToInfluxQuery(testDB) shouldEqual
      writeTester(s"precision=ns&u=${credentials.username.get}&consistency=one&db=$testDB&p=${credentials.password.get}")

    writeToInfluxQuery(testDB)(emptyCredentials) shouldEqual
      writeTester(s"db=$testDB&precision=ns&consistency=one")

    writeToInfluxQuery(testDB, consistency = Consistencys.ALL) shouldEqual
      writeTester(s"precision=ns&u=${credentials.username.get}&consistency=all&db=$testDB&p=${credentials.password.get}")

    writeToInfluxQuery(testDB, precision = Precisions.MICROSECONDS)(emptyCredentials) shouldEqual
      writeTester(s"db=$testDB&precision=u&consistency=one")
  }

  "read single query generator" should "return correct query" in {
    val map = Map[String, String](
      "db" -> testDB,
      "u" -> credentials.username.get,
      "p" -> credentials.password.get,
      "pretty" -> "false",
      "chunked" -> "false",
      "epoch" -> "ns",
      "q" -> "SELECT * FROM test"
    )
    readFromInfluxSingleQuery(testDB, testQuery) shouldEqual queryTesterSimple(map)
  }

  "read bulk query generator" should "return correct query" in {
    val map = Map[String, String](
      "db" -> testDB,
      "u" -> credentials.username.get,
      "p" -> credentials.password.get,
      "pretty" -> "false",
      "chunked" -> "false",
      "epoch" -> "ns",
      "q" -> "SELECT * FROM test;SELECT * FROM test1"
    )
    readFromInfluxBulkQuery(testDB, Seq("SELECT * FROM test", "SELECT * FROM test1")) shouldEqual queryTesterSimple(map)
  }
}
