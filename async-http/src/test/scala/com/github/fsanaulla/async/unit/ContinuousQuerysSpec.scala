package com.github.fsanaulla.async.unit

import com.github.fsanaulla.async.utils.TestHelper._
import com.github.fsanaulla.chronicler.async.handlers.AsyncQueryHandler
import com.github.fsanaulla.core.query.ContinuousQuerys
import com.github.fsanaulla.core.test.utils.TestSpec
import com.softwaremill.sttp.Uri

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class ContinuousQuerysSpec
  extends TestSpec
    with AsyncQueryHandler
    with ContinuousQuerys[Uri] {

  val host = "localhost"
  val port = 8086

  val db = "mydb"
  val cq = "bee_cq"
  val query = "SELECT mean(bees) AS mean_bees INTO aggregate_bees FROM farm GROUP BY time(30m)"

  "ContinuousQuerys operation" should "generate correct show query" in {
    showCQQuery() shouldEqual queryTesterAuth("SHOW CONTINUOUS QUERIES")

    showCQQuery()(emptyCredentials) shouldEqual queryTester("SHOW CONTINUOUS QUERIES")
  }

  it should "generate correct drop query" in {
    dropCQQuery(db, cq) shouldEqual queryTesterAuth(s"DROP CONTINUOUS QUERY $cq ON $db")

    dropCQQuery(db, cq)(emptyCredentials) shouldEqual queryTester(s"DROP CONTINUOUS QUERY $cq ON $db")
  }

  it should "generate correct create query" in {
    createCQQuery(db, cq, query) shouldEqual queryTesterAuth(s"CREATE CONTINUOUS QUERY $cq ON $db BEGIN $query END")

    createCQQuery(db, cq, query)(emptyCredentials) shouldEqual queryTester(s"CREATE CONTINUOUS QUERY $cq ON $db BEGIN $query END")
  }

}