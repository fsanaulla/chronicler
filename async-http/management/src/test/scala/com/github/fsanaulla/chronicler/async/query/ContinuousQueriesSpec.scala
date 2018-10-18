/*
 * Copyright 2017-2018 Faiaz Sanaulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fsanaulla.chronicler.async.query

import com.github.fsanaulla.chronicler.async.TestHelper._
import com.github.fsanaulla.chronicler.async.shared.handlers.AsyncQueryBuilder
import com.github.fsanaulla.chronicler.core.query.ContinuousQueries
import com.github.fsanaulla.chronicler.testing.unit.{EmptyCredentials, FlatSpecWithMatchers, NonEmptyCredentials}
import com.softwaremill.sttp.Uri

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class ContinuousQueriesSpec extends FlatSpecWithMatchers {

  trait Env extends AsyncQueryBuilder with ContinuousQueries[Uri] {
    val host = "localhost"
    val port = 8086
  }

  trait AuthEnv extends Env with NonEmptyCredentials
  trait NonAuthEnv extends Env with EmptyCredentials

  val db = "mydb"
  val cq = "bee_cq"
  val query = "SELECT mean(bees) AS mean_bees INTO aggregate_bees FROM farm GROUP BY time(30m)"

  "ContinuousQuerys operation" should "generate correct show query" in new AuthEnv {
    showCQQuery.toString() shouldEqual queryTesterAuth("SHOW CONTINUOUS QUERIES")(credentials.get)
  }

  it should "generate correct drop query" in new AuthEnv {
    dropCQQuery(db, cq).toString() shouldEqual queryTesterAuth(s"DROP CONTINUOUS QUERY $cq ON $db")(credentials.get)
  }

  it should "generate correct create query" in new AuthEnv {
    createCQQuery(db, cq, query).toString() shouldEqual queryTesterAuth(s"CREATE CONTINUOUS QUERY $cq ON $db BEGIN $query END")(credentials.get)
  }

  it should "generate correct show query without auth" in new NonAuthEnv {
    showCQQuery.toString() shouldEqual queryTester("SHOW CONTINUOUS QUERIES")
  }

  it should "generate correct drop query without auth" in new NonAuthEnv {
    dropCQQuery(db, cq).toString() shouldEqual queryTester(s"DROP CONTINUOUS QUERY $cq ON $db")
  }

  it should "generate correct create query without auth" in new NonAuthEnv {
    createCQQuery(db, cq, query).toString() shouldEqual queryTester(s"CREATE CONTINUOUS QUERY $cq ON $db BEGIN $query END")
  }
}