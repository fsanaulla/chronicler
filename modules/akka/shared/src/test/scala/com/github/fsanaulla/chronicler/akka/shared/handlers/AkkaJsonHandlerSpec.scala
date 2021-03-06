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

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import org.scalatest._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.typelevel.jawn.ast._

import scala.concurrent.ExecutionContextExecutor

class AkkaJsonHandlerSpec
    extends TestKit(ActorSystem())
    with AnyWordSpecLike
    with ScalaFutures
    with IntegrationPatience
    with Matchers
    with EitherValues
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    super.afterAll()
    mat.shutdown()
    TestKit.shutdownActorSystem(system)
  }

  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val mat: ActorMaterializer       = ActorMaterializer()

  val jsonHandler = new AkkaJsonHandler(new AkkaBodyUnmarshaller(compressed = false))

  "JsonHandler" should {
    "extract" should {
      "body from HTTP response" in {
        val singleStrJson =
          """{
            |                      "results": [
            |                          {
            |                              "statement_id": 0,
            |                              "series": [
            |                                 {
            |                                      "name": "cpu_load_short",
            |                                      "columns": [
            |                                          "time",
            |                                          "value"
            |                                      ],
            |                                      "values": [
            |                                          [
            |                                              "2015-01-29T21:55:43.702900257Z",
            |                                              2
            |                                          ],
            |                                          [
            |                                              "2015-01-29T21:55:43.702900257Z",
            |                                              0.55
            |                                          ],
            |                                          [
            |                                              "2015-06-11T20:46:02Z",
            |                                              0.64
            |                                          ]
            |                                      ]
            |                                  }
            |                              ]
            |                          }
            |                      ]
            |                  }""".stripMargin

        val resp = HttpResponse(entity = singleStrJson)

        val result: JValue = JParser.parseFromString(singleStrJson).get

        jsonHandler.responseBody(resp).futureValue.value shouldEqual result
      }

      "query result from JSON" in {
        val json =
          JParser.parseUnsafe("""
                                |{
                                |    "results": [
                                |        {
                                |            "statement_id": 0,
                                |            "series": [
                                |                {
                                |                    "name": "cpu_load_short",
                                |                    "columns": [
                                |                        "time",
                                |                        "name",
                                |                        "value"
                                |                    ],
                                |                    "values": [
                                |                        [
                                |                            "2015-01-29T21:55:43.702900257Z",
                                |                            "Fz",
                                |                            2
                                |                        ],
                                |                        [
                                |                            "2015-01-29T21:55:43.702900257Z",
                                |                            "Rz",
                                |                            0.55
                                |                        ],
                                |                        [
                                |                            "2015-06-11T20:46:02Z",
                                |                            null,
                                |                            0.64
                                |                        ]
                                |                    ]
                                |                }
                                |            ]
                                |        }
                                |    ]
                                |}
      """.stripMargin)

        val result = Array(
          JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JString("Fz"), JNum(2))),
          JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JString("Rz"), JNum(0.55))),
          JArray(Array(JString("2015-06-11T20:46:02Z"), JNull, JNum(0.64)))
        )

        jsonHandler.queryResult(json).get shouldEqual result
      }

      "query result from empty JSON" in {
        val json = JParser.parseUnsafe("""{"results":[{"statement_id":0}]}""")

        jsonHandler.queryResult(json) shouldEqual None
      }

      "bulk query result from JSON" in {
        val json =
          JParser.parseUnsafe("""
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
      """.stripMargin)

        val result = Array(
          Array(
            JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
            JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
            JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))
          ),
          Array(
            JArray(Array(JString("1970-01-01T00:00:00Z"), JNum(3)))
          )
        )

        jsonHandler.bulkResult(json).get shouldEqual result
      }

      "bulk query result partially empty from JSON" in {
        val json =
          JParser.parseUnsafe("""
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
                                |        {"statement_id": 1}
                                |    ]
                                |}""".stripMargin)

        val result = Array(
          Array(
            JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
            JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
            JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))
          )
        )

        jsonHandler.bulkResult(json).get shouldEqual result
      }

      "empty bulk query result from JSON" in {
        val json =
          JParser.parseUnsafe(
            """{"results": [{"statement_id": 0},{"statement_id": 1}] }""".stripMargin
          )

        jsonHandler.bulkResult(json).get shouldEqual Array.empty[Array[JArray]]
      }

      "grouped system query result from JSON" in {
        val json =
          JParser.parseUnsafe("""
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
      """.stripMargin)

        val result = Array(
          "cpu_load_short" -> Array(
            JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
            JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
            JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))
          )
        )

        val res = jsonHandler.groupedSystemInfoJs(json).get

        res.length shouldEqual 1
        val (measurament, points) = res.head

        measurament shouldEqual "cpu_load_short"
        points shouldEqual result.head._2
      }

      "grouped query result from JSON" in {
        val json = JParser.parseUnsafe("""
                                         |{
                                         |   "results": [
                                         |     {
                                         |         "statement_id": 0,
                                         |         "series": [
                                         |           {
                                         |             "name": "cpu_load_short",
                                         |             "tags": {
                                         |               "host": "server01",
                                         |               "region": "us-west"
                                         |             },
                                         |             "columns": [
                                         |               "time",
                                         |               "mean"
                                         |             ],
                                         |             "values": [
                                         |               [
                                         |                 "1970-01-01T00:00:00Z",
                                         |                 0.69
                                         |               ]
                                         |             ]
                                         |           },
                                         |           {
                                         |             "name": "cpu_load_short",
                                         |             "tags": {
                                         |               "host": "server02",
                                         |               "region": "us-west"
                                         |             },
                                         |             "columns": [
                                         |               "time",
                                         |               "mean"
                                         |             ],
                                         |             "values": [
                                         |               [
                                         |                 "1970-01-01T00:00:00Z",
                                         |                 0.73
                                         |               ]
                                         |             ]
                                         |           }
                                         |         ]
                                         |     }
                                         |   ]
                                         |}
      """.stripMargin)

        val groupedResult = jsonHandler.groupedResult(json)

        groupedResult should not be None

        val result = groupedResult.get
        result.length shouldEqual 2

        result.map { case (k, v) => k.toList -> v.toList }.toList shouldEqual List(
          List("server01", "us-west") -> List(
            JArray(Array(JString("1970-01-01T00:00:00Z"), JNum(0.69)))
          ),
          List("server02", "us-west") -> List(
            JArray(Array(JString("1970-01-01T00:00:00Z"), JNum(0.73)))
          )
        )
      }
    }
  }
}
