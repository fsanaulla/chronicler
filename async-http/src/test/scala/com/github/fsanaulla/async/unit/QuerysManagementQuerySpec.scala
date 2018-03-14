package com.github.fsanaulla.async.unit

import com.github.fsanaulla.async.utils.TestHelper._
import com.github.fsanaulla.chronicler.async.handlers.AsyncQueryHandler
import com.github.fsanaulla.core.query.QuerysManagementQuery
import com.github.fsanaulla.core.test.utils.{EmptyCredentials, NonEmptyCredentials, TestSpec}
import com.softwaremill.sttp.Uri

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class QuerysManagementQuerySpec extends TestSpec {

  trait Env extends AsyncQueryHandler with QuerysManagementQuery[Uri] {
    val host = "localhost"
    val port = 8086
  }
  trait AuthEnv extends Env with NonEmptyCredentials
  trait NonAuthEnv extends Env with EmptyCredentials

  "QueryManagement" should "show query" in new AuthEnv {
    showQuerysQuery() shouldEqual queryTesterAuth("SHOW QUERIES")(credentials.get)
  }

  it should "kill query" in new AuthEnv {
    killQueryQuery(5) shouldEqual queryTesterAuth("KILL QUERY 5")(credentials.get)
  }

  it should "show query without auth" in new NonAuthEnv {
    showQuerysQuery() shouldEqual queryTester("SHOW QUERIES")
  }

  it should "kill query without auth" in new NonAuthEnv {
    killQueryQuery(5) shouldEqual queryTester("KILL QUERY 5")
  }
}
