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

package com.github.fsanaulla.chronicler.akka.query

import _root_.akka.http.scaladsl.model.Uri
import com.github.fsanaulla.chronicler.akka.TestHelper._
import com.github.fsanaulla.chronicler.akka.shared.handlers.AkkaQueryBuilder
import com.github.fsanaulla.chronicler.core.enums.{Consistencies, Epochs, Precisions}
import com.github.fsanaulla.chronicler.core.model.HasCredentials
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import com.github.fsanaulla.chronicler.testing.unit.{EmptyCredentials, FlatSpecWithMatchers, NonEmptyCredentials}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class DatabaseOperationQuerySpec extends FlatSpecWithMatchers {

  trait Env extends AkkaQueryBuilder with DatabaseOperationQuery[Uri] { self: HasCredentials =>
    val host = "localhost"
    val port = 8086
  }
  trait AuthEnv extends Env with NonEmptyCredentials
  trait NonAuthEnv extends Env with EmptyCredentials

  val testDB = "db"
  val testQuery = "SELECT * FROM test"


  "DatabaseOperationQuery" should "return correct write query" in new AuthEnv {

    writeToInfluxQuery(testDB, Consistencies.ONE, Precisions.NANOSECONDS, None) shouldEqual writeTester(
      Map(
        "precision" -> "ns",
        "u" -> credentials.get.username,
        "consistency" -> "one",
        "db" -> testDB,
        "p" -> credentials.get.password
      )
    )

    writeToInfluxQuery(testDB, Consistencies.ALL, Precisions.NANOSECONDS, None) shouldEqual writeTester(
      Map(
        "precision" -> "ns",
        "u" -> credentials.get.username,
        "consistency" -> "all",
        "db" -> testDB,
        "p" -> credentials.get.password)
    )
  }

  it should "return correct write query without auth " in new NonAuthEnv {
    writeToInfluxQuery(testDB, Consistencies.ONE, Precisions.NANOSECONDS, None) shouldEqual
      writeTester(Map("db" -> testDB, "precision" -> "ns", "consistency" -> "one"))

    writeToInfluxQuery(testDB, Consistencies.ONE, Precisions.MICROSECONDS, None) shouldEqual
      writeTester(Map("db" -> testDB, "precision" -> "u", "consistency" -> "one"))
  }

  it should "return correct single read query" in new AuthEnv {
    val map: Map[String, String] = Map[String, String](
      "db" -> testDB,
      "u" -> credentials.get.username,
      "p" -> credentials.get.password,
      "pretty" -> "false",
      "chunked" -> "false",
      "epoch" -> "ns",
      "q" -> "SELECT * FROM test"
    )
    readFromInfluxSingleQuery(testDB, testQuery, Epochs.NANOSECONDS, pretty = false, chunked = false) shouldEqual queryTesterSimple(map)
  }

  it should "return correct bulk read query" in new AuthEnv {
    val map: Map[String, String] = Map[String, String](
      "db" -> testDB,
      "u" -> credentials.get.username,
      "p" -> credentials.get.password,
      "pretty" -> "false",
      "chunked" -> "false",
      "epoch" -> "ns",
      "q" -> "SELECT * FROM test;SELECT * FROM test1"
    )
    readFromInfluxBulkQuery(testDB, Seq("SELECT * FROM test", "SELECT * FROM test1"), Epochs.NANOSECONDS, pretty = false, chunked = false) shouldEqual queryTesterSimple(map)
  }
}
