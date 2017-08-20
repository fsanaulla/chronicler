package com.fsanaulla.unit

import com.fsanaulla.query.QuerysManagementQuery
import com.fsanaulla.utils.TestHelper._
import com.fsanaulla.utils.TestSpec

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class QuerysManagementQuerySpec extends TestSpec with QuerysManagementQuery {

  "show query" should "correctly work" in {
    showQuerysQuery() shouldEqual queryTesterAuth("SHOW QUERIES")

    showQuerysQuery()(emptyCredentials) shouldEqual queryTester("SHOW QUERIES")
  }

  "kill query" should "correctly work" in {
    killQueryQuery(5) shouldEqual queryTesterAuth("KILL QUERY 5")

    killQueryQuery(5)(emptyCredentials) shouldEqual queryTester("KILL QUERY 5")
  }
}
