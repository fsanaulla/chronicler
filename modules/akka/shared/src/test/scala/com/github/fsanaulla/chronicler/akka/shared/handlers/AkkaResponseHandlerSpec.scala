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

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.model.HttpResponse
import _root_.akka.stream.ActorMaterializer
import _root_.akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.shared.Extensions.RichStringOps
import com.github.fsanaulla.chronicler.core.implicits._
import com.github.fsanaulla.chronicler.core.model.ContinuousQuery
import jawn.ast.{JArray, JNum, JString}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FlatSpecLike, Matchers}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by fayaz on 12.07.17.
  */
class AkkaResponseHandlerSpec
  extends TestKit(ActorSystem())
    with FlatSpecLike
    with Matchers
    with ScalaFutures
    with IntegrationPatience {

  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val timeout: FiniteDuration = 1 second
  val rh = new AkkaResponseHandler()

  "AsyncHttpResponseHandler" should "extract single query queryResult from response" in {

    val singleHttpResponse: HttpResponse =
      """
        |{
        |    "results": [
        |        {
        |            "statement_id": 0,
        |            "series": [
        |                {
        |                    "name": "cpu_load_short",
        |                    "columns": [
        |                        "time",
        |                        "value"
        |                    ],
        |                    "values": [
        |                        [
        |                            "2015-01-29T21:55:43.702900257Z",
        |                            2
        |                        ],
        |                        [
        |                            "2015-01-29T21:55:43.702900257Z",
        |                            0.55
        |                        ],
        |                        [
        |                            "2015-06-11T20:46:02Z",
        |                            0.64
        |                        ]
        |                    ]
        |                }
        |            ]
        |        }
        |    ]
        |}
      """.stripMargin.toResponse

    val result = Array(
      JArray(Array(
        JString("2015-01-29T21:55:43.702900257Z"),
        JNum(2))),
      JArray(Array(
        JString("2015-01-29T21:55:43.702900257Z"),
        JNum(0.55))),
      JArray(Array(
        JString("2015-06-11T20:46:02Z"),
        JNum(0.64)))
    )

    rh.toQueryJsResult(singleHttpResponse).futureValue.queryResult shouldEqual result
  }

  it should "extract bulk query results from response" in {

    val bulkHttpResponse: HttpResponse =
      """
        |{
        |    "results": [
        |        {
        |            "statement_id": 0,
        |            "series": [
        |                {
        |                    "name": "cpu_load_short",
        |                    "columns": [
        |                        "time",
        |                        "value"
        |                    ],
        |                    "values": [
        |                        [
        |                            "2015-01-29T21:55:43.702900257Z",
        |                            2
        |                        ],
        |                        [
        |                            "2015-01-29T21:55:43.702900257Z",
        |                            0.55
        |                        ],
        |                        [
        |                            "2015-06-11T20:46:02Z",
        |                            0.64
        |                        ]
        |                    ]
        |                }
        |            ]
        |        },
        |        {
        |            "statement_id": 1,
        |            "series": [
        |                {
        |                    "name": "cpu_load_short",
        |                    "columns": [
        |                        "time",
        |                        "count"
        |                    ],
        |                    "values": [
        |                        [
        |                            "1970-01-01T00:00:00Z",
        |                            3
        |                        ]
        |                    ]
        |                }
        |            ]
        |        }
        |    ]
        |}
      """.stripMargin.toResponse

    rh.toBulkQueryJsResult(bulkHttpResponse).futureValue.queryResult shouldEqual Array(
      Array(
        JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
        JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
        JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))),
      Array(
        JArray(Array(JString("1970-01-01T00:00:00Z"), JNum(3)))
      )
    )
  }

  it should "cq unpacking" in {

    val cqResponse = """{
      "results": [
        {
          "statement_id": 0,
          "series": [
            {
              "name": "_internal",
              "columns": [
                "name",
                "query"
              ]
            },
            {
              "name": "my_test_db",
              "columns": [
                "name",
                "query"
              ]
            },
            {
              "name": "mydb",
              "columns": [
                "name",
                "query"
              ],
              "values": [
                [
                  "cq",
                  "CREATE CONTINUOUS QUERY cq ON mydb BEGIN SELECT mean(value) AS mean_value INTO mydb.autogen.aggregate FROM mydb.autogen.cpu_load_short GROUP BY time(30m) END"
                ]
              ]
            },
            {
              "name": "db",
              "columns": [
                "name",
                "query"
              ]
            },
            {
              "name": "fz_db",
              "columns": [
                "name",
                "query"
              ]
            }
          ]
        }
      ]
    }
  """.toResponse

    val cqi = rh.toCqQueryResult(cqResponse).futureValue.queryResult.filter(_.querys.nonEmpty).head
    cqi.dbName shouldEqual "mydb"
    cqi.querys.head shouldEqual ContinuousQuery("cq", "CREATE CONTINUOUS QUERY cq ON mydb BEGIN SELECT mean(value) AS mean_value INTO mydb.autogen.aggregate FROM mydb.autogen.cpu_load_short GROUP BY time(30m) END")
  }

  it should "extract optional error message" in {

    val errorHttpResponse: HttpResponse =
      """
        |{
        |        "results": [
        |          {
        |            "statement_id": 0,
        |            "error": "user not found"
        |          }
        |        ]
        |}
      """.stripMargin.toResponse

    rh.getOptResponseError(errorHttpResponse).futureValue shouldEqual Some("user not found")
  }

  it should "extract error message" in {

    val errorHttpResponse: HttpResponse =
      """ { "error": "user not found" } """.toResponse

    rh.getResponseError(errorHttpResponse).futureValue shouldEqual "user not found"
  }
}
