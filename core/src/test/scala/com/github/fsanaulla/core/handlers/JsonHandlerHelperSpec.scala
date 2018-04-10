package com.github.fsanaulla.core.handlers

import com.github.fsanaulla.core.handlers.json.JsonHandlerHelper
import com.github.fsanaulla.core.test.FlatSpecWithMatchers
import jawn.ast.{JArray, JNum, JParser, JString}
import org.scalatest.OptionValues

class JsonHandlerHelperSpec extends FlatSpecWithMatchers with OptionValues with JsonHandlerHelper {

  "JsonHandler" should "extract single query result from JSON" in {

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
      JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
      JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
      JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))
    )

    getOptInfluxPoints(json).value shouldEqual result
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
    points shouldEqual Array(
      JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
      JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
      JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))
    )
  }
}
