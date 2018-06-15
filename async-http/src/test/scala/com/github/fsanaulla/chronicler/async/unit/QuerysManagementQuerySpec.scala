package com.github.fsanaulla.chronicler.async.unit

import com.github.fsanaulla.chronicler.async.handlers.AsyncQueryHandler
import com.github.fsanaulla.chronicler.async.utils.TestHelper._
import com.github.fsanaulla.chronicler.core.query.QuerysManagementQuery
import com.github.fsanaulla.chronicler.testing.{EmptyCredentials, FlatSpecWithMatchers, NonEmptyCredentials}
import com.softwaremill.sttp.Uri

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class QuerysManagementQuerySpec extends FlatSpecWithMatchers {

  trait Env extends AsyncQueryHandler with QuerysManagementQuery[Uri] {
    val host = "localhost"
    val port = 8086
  }
  trait AuthEnv extends Env with NonEmptyCredentials
  trait NonAuthEnv extends Env with EmptyCredentials

  "QueryManagement" should "show query" in new AuthEnv {
    showQuerysQuery().toString() shouldEqual queryTesterAuth("SHOW QUERIES")(credentials.get)
  }

  it should "kill query" in new AuthEnv {
    killQueryQuery(5).toString() shouldEqual queryTesterAuth("KILL QUERY 5")(credentials.get)
  }

  it should "show query without auth" in new NonAuthEnv {
    showQuerysQuery().toString() shouldEqual queryTester("SHOW QUERIES")
  }

  it should "kill query without auth" in new NonAuthEnv {
    killQueryQuery(5).toString() shouldEqual queryTester("KILL QUERY 5")
  }
}
