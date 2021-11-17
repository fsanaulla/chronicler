/*
 * Copyright 2017-2019 Faiaz Sanaulla
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

import com.github.fsanaulla.chronicler.akka.shared.AkkaQueryBuilder
import com.github.fsanaulla.chronicler.core.enums.{Consistencies, Epochs, Precisions}
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.model.Uri

/** Created by Author: fayaz.sanaulla@gmail.com Date: 27.07.17
  */
class DatabaseOperationQuerySpec
    extends AnyFlatSpec
    with Matchers
    with DatabaseOperationQuery[Uri] {

  val testDB      = "db"
  val testQuery   = "SELECT * FROM test"
  implicit val qb = new AkkaQueryBuilder("localhost", 8086)

  "DatabaseOperationQuery" should "return correct write query" in {

    write(testDB, Consistencies.One, Precisions.Nanoseconds, None)
      .toString() shouldEqual queryTester(
      "/write",
      List(
        "consistency" -> "one",
        "precision"   -> "ns",
        "db"          -> testDB
      )
    )

    write(testDB, Consistencies.All, Precisions.Nanoseconds, None)
      .toString() shouldEqual queryTester(
      "/write",
      List(
        "consistency" -> "all",
        "precision"   -> "ns",
        "db"          -> testDB
      )
    )
  }

  it should "return correct single read query" in {

    val queryPrms: List[(String, String)] = List(
      "epoch" -> "ns",
      "q"     -> "SELECT * FROM test",
      "db"    -> testDB
    )
    singleQuery(testDB, testQuery, Epochs.Nanoseconds, pretty = false).toString() shouldEqual
      queryTester("/query", queryPrms)
  }

  it should "return correct bulk read query" in {

    val queryPrms: List[(String, String)] = List(
      "epoch" -> "ns",
      "q"     -> "SELECT * FROM test;SELECT * FROM test1",
      "db"    -> testDB
    )
    bulkQuery(
      testDB,
      Seq("SELECT * FROM test", "SELECT * FROM test1"),
      Epochs.Nanoseconds,
      pretty = false
    ).toString() shouldEqual queryTester("/query", queryPrms)

    val queryPrms1: List[(String, String)] = List(
      "pretty" -> "true",
      "epoch"  -> "ns",
      "q"      -> "SELECT * FROM test;SELECT * FROM test1",
      "db"     -> testDB
    )
    bulkQuery(
      testDB,
      Seq("SELECT * FROM test", "SELECT * FROM test1"),
      Epochs.Nanoseconds,
      pretty = true
    ).toString() shouldEqual queryTester("/query", queryPrms1)
  }
}
