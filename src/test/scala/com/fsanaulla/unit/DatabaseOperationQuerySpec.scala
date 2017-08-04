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

  "write query" should "return correct query" in {
    writeToInfluxQuery(testDB, optTestUsername, optTestPass) shouldEqual
      writeTester(s"precision=ns&u=${optTestUsername.get}&consistency=one&db=$testDB&p=${optTestPass.get}")

    writeToInfluxQuery(testDB, optTestUsername) shouldEqual
      writeTester(s"db=$testDB&precision=ns&consistency=one")

    writeToInfluxQuery(testDB, optTestUsername, optTestPass, consistency = Consistencys.ALL) shouldEqual
      writeTester(s"precision=ns&u=${optTestUsername.get}&consistency=all&db=$testDB&p=${optTestPass.get}")

    writeToInfluxQuery(testDB, precision = Precisions.MICROSECONDS) shouldEqual
      writeTester(s"db=$testDB&precision=u&consistency=one")
  }
}
