package com.github.fsanaulla.chronicler.akka.unit

import akka.http.scaladsl.model.Uri
import com.github.fsanaulla.chronicler.akka.handlers.AkkaQueryHandler
import com.github.fsanaulla.chronicler.akka.utils.TestHelper._
import com.github.fsanaulla.core.query.ContinuousQuerys
import com.github.fsanaulla.core.test.utils.TestSpec

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class ContinuousQuerysSpec
  extends TestSpec
    with TestCredenctials
    with AkkaQueryHandler
    with ContinuousQuerys[Uri] {

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