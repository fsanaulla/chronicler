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

package com.github.fsanaulla.chronicler.ahc.shared.handlers

import org.scalatest.{FlatSpec, Matchers}

class AhcQueryBuilderSpec extends FlatSpec with Matchers {

  val host = "localhost"
  val port = 8080

  val qb = new AhcQueryBuilder("http", host, port, None)

  it should "properly generate URI" in {
    val queryMap = List(
      "q" -> "FirstQuery;SecondQuery"
    )
    val res = s"http://$host:$port/query?q=FirstQuery%3BSecondQuery"

    qb.buildQuery("/query", qb.appendCredentials(queryMap)).mkUrl shouldEqual res
  }

}
