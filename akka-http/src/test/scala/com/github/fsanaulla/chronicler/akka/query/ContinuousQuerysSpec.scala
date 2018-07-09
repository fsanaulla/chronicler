package com.github.fsanaulla.chronicler.akka.query

import _root_.akka.http.scaladsl.model.Uri
import com.github.fsanaulla.chronicler.akka.TestHelper._
import com.github.fsanaulla.chronicler.akka.handlers.AkkaQueryHandler
import com.github.fsanaulla.chronicler.core.model.HasCredentials
import com.github.fsanaulla.chronicler.core.query.ContinuousQuerys
import com.github.fsanaulla.chronicler.testing.unit.{EmptyCredentials, FlatSpecWithMatchers, NonEmptyCredentials}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class ContinuousQuerysSpec extends FlatSpecWithMatchers {

  trait Env extends AkkaQueryHandler with ContinuousQuerys[Uri] { self: HasCredentials =>
    val host = "localhost"
    val port = 8086
  }

  trait AuthEnv extends Env with NonEmptyCredentials
  trait NonAuthEnv extends Env with EmptyCredentials

  val db = "mydb"
  val cq = "bee_cq"
  val query = "SELECT mean(bees) AS mean_bees INTO aggregate_bees FROM farm GROUP BY time(30m)"

  "ContinuousQuerys operation" should "generate correct show query" in new AuthEnv {
    showCQQuery() shouldEqual queryTesterAuth("SHOW CONTINUOUS QUERIES")(credentials.get)
  }

  it should "generate correct drop query" in new AuthEnv {
    dropCQQuery(db, cq) shouldEqual queryTesterAuth(s"DROP CONTINUOUS QUERY $cq ON $db")(credentials.get)
  }

  it should "generate correct create query" in new AuthEnv {
    createCQQuery(db, cq, query) shouldEqual queryTesterAuth(s"CREATE CONTINUOUS QUERY $cq ON $db BEGIN $query END")(credentials.get)
  }

  it should "generate correct show query without auth" in new NonAuthEnv {
    showCQQuery() shouldEqual queryTester("SHOW CONTINUOUS QUERIES")
  }

  it should "generate correct drop query without auth" in new NonAuthEnv {
    dropCQQuery(db, cq) shouldEqual queryTester(s"DROP CONTINUOUS QUERY $cq ON $db")
  }

  it should "generate correct create query without auth" in new NonAuthEnv {
    createCQQuery(db, cq, query) shouldEqual queryTester(s"CREATE CONTINUOUS QUERY $cq ON $db BEGIN $query END")
  }
}