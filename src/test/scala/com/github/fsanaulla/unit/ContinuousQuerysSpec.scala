package com.github.fsanaulla.unit

import com.github.fsanaulla.query.ContinuousQuerys
import com.github.fsanaulla.utils.TestCredentials
import com.github.fsanaulla.utils.TestHelper._
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class ContinuousQuerysSpec
  extends FlatSpec
    with Matchers
    with ContinuousQuerys
    with TestCredentials {

  val db = "mydb"
  val cq = "bee_cq"
  val query = "SELECT mean(bees) AS mean_bees INTO aggregate_bees FROM farm GROUP BY time(30m)"

  "show CQ query" should "generate correct query" in {
    showCQQuery() shouldEqual queryTesterAuth("SHOW CONTINUOUS QUERIES")

    showCQQuery()(emptyCredentials) shouldEqual queryTester("SHOW CONTINUOUS QUERIES")
  }

  "drop CQ query" should "generate correct query" in {
    dropCQQuery(db, cq) shouldEqual queryTesterAuth(s"DROP CONTINUOUS QUERY $cq ON $db")

    dropCQQuery(db, cq)(emptyCredentials) shouldEqual queryTester(s"DROP CONTINUOUS QUERY $cq ON $db")
  }

  "create CQ query" should "generate correct query" in {
    createCQQuery(db, cq, query) shouldEqual queryTesterAuth(s"CREATE CONTINUOUS QUERY $cq ON $db BEGIN $query END")

    createCQQuery(db, cq, query)(emptyCredentials) shouldEqual queryTester(s"CREATE CONTINUOUS QUERY $cq ON $db BEGIN $query END")
  }

}
