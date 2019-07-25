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

import com.github.fsanaulla.chronicler.core.model.InfluxCredentials
import org.scalatest.{Matchers, WordSpec}

class AkkaQueryBuilderSpec extends WordSpec with Matchers {

  implicit val credentials: Option[InfluxCredentials] = None
  implicit val nonEmptyCredentials: Some[InfluxCredentials] =
    Some(InfluxCredentials("admin", "admin"))

  val queryMap: List[(String, String)] = List(
    "q" -> "FirstQuery;SecondQuery"
  )

  "Query handler" should {
    "build http connection url" should {
      "without credentials " in {
        val qb: AkkaQueryBuilder = new AkkaQueryBuilder("http", "localhost", 8086, credentials)
        val res                  = s"http://localhost:8086/query?q=FirstQuery%3BSecondQuery"
        qb.buildQuery("/query", qb.appendCredentials(queryMap)).toString() shouldEqual res
      }

      "with credentials" in {
        val qb: AkkaQueryBuilder =
          new AkkaQueryBuilder("http", "localhost", 8086, nonEmptyCredentials)
        val res = s"http://localhost:8086/query?u=admin&p=admin&q=FirstQuery%3BSecondQuery"
        qb.buildQuery("/query", qb.appendCredentials(queryMap)).toString() shouldEqual res
      }

      "without query params" in {
        val qb: AkkaQueryBuilder =
          new AkkaQueryBuilder("http", "localhost", 8086, nonEmptyCredentials)
        val res = s"http://localhost:8086/write?u=admin&p=admin"
        qb.buildQuery("/write", qb.appendCredentials(Nil)).toString() shouldEqual res
      }
    }

    "build https connection url" should {
      "without credentials" in {
        val qb: AkkaQueryBuilder =
          new AkkaQueryBuilder("https", "localhost", 8086, credentials)
        val res = s"https://localhost:8086/query?q=FirstQuery%3BSecondQuery"
        qb.buildQuery("/query", qb.appendCredentials(queryMap)).toString() shouldEqual res
      }

      "with credentials" in {
        val qb: AkkaQueryBuilder =
          new AkkaQueryBuilder("https", "localhost", 8086, nonEmptyCredentials)
        val res = s"https://localhost:8086/query?u=admin&p=admin&q=FirstQuery%3BSecondQuery"
        qb.buildQuery("/query", qb.appendCredentials(queryMap)).toString() shouldEqual res
      }

      "without query params" in {
        val qb: AkkaQueryBuilder =
          new AkkaQueryBuilder("https", "localhost", 8086, nonEmptyCredentials)
        val res = s"https://localhost:8086/write?u=admin&p=admin"
        qb.buildQuery("/write", qb.appendCredentials(Nil)).toString() shouldEqual res
      }
    }
  }
}
