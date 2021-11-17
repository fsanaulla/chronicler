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
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.github.fsanaulla.chronicler.core.management.cq.ContinuousQueries
import sttp.model.Uri

/** Created by Author: fayaz.sanaulla@gmail.com Date: 10.08.17
  */
class ContinuousQueriesSpec extends AnyFlatSpec with Matchers with ContinuousQueries[Uri] {

  val db    = "mydb"
  val cq    = "bee_cq"
  val query = "SELECT mean(bees) AS mean_bees INTO aggregate_bees FROM farm GROUP BY time(30m)"
  implicit val qb = new AkkaQueryBuilder("localhost", 8086)

  "ContinuousQuerys operation" should "generate correct show query" in {
    showCQQuery.toString shouldEqual queryTester("SHOW CONTINUOUS QUERIES")
  }

  it should "generate correct drop query" in {
    dropCQQuery(db, cq).toString shouldEqual queryTester(s"DROP CONTINUOUS QUERY $cq ON $db")
  }

  it should "generate correct create query" in {
    createCQQuery(db, cq, query).toString shouldEqual queryTester(
      s"CREATE CONTINUOUS QUERY $cq ON $db BEGIN $query END"
    )
  }
}
