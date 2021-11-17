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

package com.github.fsanaulla.chronicler.akka.shared.handlers

import com.github.fsanaulla.chronicler.akka.shared.AkkaQueryBuilder
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.github.fsanaulla.chronicler.core.auth.InfluxCredentials
import com.github.fsanaulla.chronicler.testing.BaseSpec

class AkkaQueryBuilderSpec extends BaseSpec {

  "Query handler" - {

    val qb: AkkaQueryBuilder = new AkkaQueryBuilder("localhost", 8086)

    "build uri" - {

      "without query params " in {
        val res = s"http://localhost:8086/query"
        qb.buildQuery("/query").toString() shouldEqual res
      }

      "with query params" in {
        val queryMap: List[(String, String)] = List(
          "u" -> "admin",
          "p" -> "admin",
          "q" -> "FirstQuery;SecondQuery"
        )
        val res = s"http://localhost:8086/query?u=admin&p=admin&q=FirstQuery%3BSecondQuery"
        qb.buildQuery("/query", queryMap).toString() shouldEqual res
      }
    }
  }
}
