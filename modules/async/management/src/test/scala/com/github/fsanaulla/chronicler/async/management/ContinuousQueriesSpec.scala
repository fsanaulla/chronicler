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

package com.github.fsanaulla.chronicler.async.management

import com.github.fsanaulla.chronicler.async.shared.AsyncQueryBuilder
import com.github.fsanaulla.chronicler.core.management.cq.ContinuousQueries
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.model.Uri
import com.github.fsanaulla.chronicler.async.shared.AsyncQueryBuilder
import com.github.fsanaulla.chronicler.testing.BaseSpec

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class ContinuousQueriesSpec extends BaseSpec with ContinuousQueries[Uri] {

  implicit val qb = new AsyncQueryBuilder("localhost", 8086)

  val db    = "mydb"
  val cq    = "bee_cq"
  val query = "SELECT mean(bees) AS mean_bees INTO aggregate_bees FROM farm GROUP BY time(30m)"

  "ContinuousQuerys operation" - {

    "should" - {
      
      "generate correct show query" in {
        showCQQuery.toString() shouldEqual queryTester("SHOW CONTINUOUS QUERIES")
      }

      "generate correct drop query" in {
        dropCQQuery(db, cq).toString() shouldEqual queryTester(s"DROP CONTINUOUS QUERY $cq ON $db")
      }

      "generate correct create query" in {
        createCQQuery(db, cq, query).toString() shouldEqual queryTester(
          s"CREATE CONTINUOUS QUERY $cq ON $db BEGIN $query END"
        )
      }

      "generate correct show query without auth" in {
        showCQQuery.toString() shouldEqual queryTester("SHOW CONTINUOUS QUERIES")
      }

      "generate correct drop query without auth" in {
        dropCQQuery(db, cq).toString() shouldEqual queryTester(s"DROP CONTINUOUS QUERY $cq ON $db")
      }

      "generate correct create query without auth" in {
        createCQQuery(db, cq, query).toString() shouldEqual queryTester(
          s"CREATE CONTINUOUS QUERY $cq ON $db BEGIN $query END"
        )
      }
    }
  }
}
