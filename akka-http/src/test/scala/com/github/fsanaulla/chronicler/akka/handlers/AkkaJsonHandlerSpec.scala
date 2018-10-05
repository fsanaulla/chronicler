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

package com.github.fsanaulla.chronicler.akka.handlers

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import _root_.akka.stream.ActorMaterializer
import _root_.akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.FuturesHandler
import com.github.fsanaulla.chronicler.akka.utils.AkkaContentTypes.AppJson
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers
import jawn.ast._
import org.scalatest.{BeforeAndAfterAll, OptionValues, TryValues}

import scala.concurrent.ExecutionContext

class AkkaJsonHandlerSpec
  extends TestKit(ActorSystem())
    with FlatSpecWithMatchers
    with FuturesHandler
    with OptionValues
    with AkkaJsonHandler
    with BeforeAndAfterAll
    with TryValues {

  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ex: ExecutionContext = system.dispatcher

  override def afterAll: Unit = {
    super.afterAll()
    TestKit.shutdownActorSystem(system)
  }

  "AkkaJsonHandler" should "extract js object from HTTP response" in {
    val singleStrJson = """{
                      "results": [
                          {
                              "statement_id": 0,
                              "series": [
                                 {
                                      "name": "cpu_load_short",
                                      "columns": [
                                          "time",
                                          "value"
                                      ],
                                      "values": [
                                          [
                                              "2015-01-29T21:55:43.702900257Z",
                                              2
                                          ],
                                          [
                                              "2015-01-29T21:55:43.702900257Z",
                                              0.55
                                          ],
                                          [
                                              "2015-06-11T20:46:02Z",
                                              0.64
                                          ]
                                      ]
                                  }
                              ]
                          }
                      ]
                  }"""

  val singleHttpResponse: HttpResponse =
    HttpResponse(entity = HttpEntity(AppJson, singleStrJson))

  val jsResult: JValue =
    JParser.parseFromString(singleStrJson).toOption.value

    getResponseBody(singleHttpResponse).futureValue shouldEqual jsResult
  }

  it should "extract single query result from JSON" in {

    val json  = JParser.parseFromString(
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
      """.stripMargin).toOption.value

    val result = Array(
      JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JString("Fz"), JNum(2))),
      JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JString("Rz"), JNum(0.55))),
      JArray(Array(JString("2015-06-11T20:46:02Z"), JNull, JNum(0.64)))
    )

    getOptQueryResult(json).value shouldEqual result
  }

  it should "extract bulk query result from JSON" in {
    val json = JParser.parseFromString(
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
      """.stripMargin).toOption.value

    val result = Array(
      Array(
        JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
        JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
        JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))),
      Array(
        JArray(Array(JString("1970-01-01T00:00:00Z"), JNum(3)))
      )
    )

    getOptBulkInfluxPoints(json).value shouldEqual result
  }

  it should "extract influx information from JSON" in {
    val json  = JParser.parseFromString(
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
      """.stripMargin).toOption.value

    val result = Array(
      "cpu_load_short" -> Array(
        JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
        JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
        JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))
      )
    )

    val res = getOptJsInfluxInfo(json)

    res should not be None
    res.value.length shouldEqual 1
    val (measurament, points) = res.value.head

    measurament shouldEqual "cpu_load_short"
    points shouldEqual result.head._2
  }

  it should "extract grouped result" in {
    val json = JParser.parseFromString(
      """
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
      """.stripMargin).success.value

    val optResult = getOptGropedResult(json)

    optResult should not be None

    val result = optResult.value
    result.length shouldEqual 2

    result.map { case (k, v) => k.toList -> v}.toList shouldEqual List(
     List("server01", "us-west") -> JArray(Array(JString("1970-01-01T00:00:00Z"), JNum(0.69))),
     List("server02", "us-west") -> JArray(Array(JString("1970-01-01T00:00:00Z"), JNum(0.73)))
    )
  }
}
