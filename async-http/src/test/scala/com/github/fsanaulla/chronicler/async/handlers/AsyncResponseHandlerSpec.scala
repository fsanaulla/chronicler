package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.chronicler.async.TestExtensions.{RichString, RichTry}
import com.github.fsanaulla.chronicler.core.model.ContinuousQuery
import com.github.fsanaulla.chronicler.core.utils.DefaultInfluxImplicits._
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers
import com.softwaremill.sttp.Response
import jawn.ast._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Second, Seconds, Span}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class AsyncResponseHandlerSpec extends FlatSpecWithMatchers with AsyncResponseHandler with ScalaFutures {

  implicit val pc: PatienceConfig = PatienceConfig(Span(20, Seconds), Span(1, Second))

  protected implicit val ex: ExecutionContext = ExecutionContext.Implicits.global

  implicit val timeout: FiniteDuration = 1 second

  implicit val p: JParser.type = JParser


  "AsyncResponseHandler" should "extract single query result from response" in {

    val singleResponse: Response[JValue] =
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

    toQueryJsResult(singleResponse).futureValue.queryResult shouldEqual result
  }

  it should "extract bulk query results from response" in {

    val bulkResponse: Response[JValue] =
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
      """.stripMargin.toResponse()

    toBulkQueryJsResult(bulkResponse).futureValue.queryResult shouldEqual Array(
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

    val cqStrJson = """{
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
  """
    val cqHttpResponse = Response(p.parseFromString(cqStrJson).toStrEither(cqStrJson), 200, "", Nil, Nil)

    val cqi = toCqQueryResult(cqHttpResponse).futureValue.queryResult.filter(_.querys.nonEmpty).head
    cqi.dbName shouldEqual "mydb"
    cqi.querys.head shouldEqual ContinuousQuery("cq", "CREATE CONTINUOUS QUERY cq ON mydb BEGIN SELECT mean(value) AS mean_value INTO mydb.autogen.aggregate FROM mydb.autogen.cpu_load_short GROUP BY time(30m) END")
  }

  it should "extract optional error message" in {

    val errorResponse: Response[JValue] =
      """
        |{
        |        "results": [
        |          {
        |            "statement_id": 0,
        |            "error": "user not found"
        |          }
        |        ]
        |}
      """.stripMargin.toResponse()

    getOptResponseError(errorResponse).futureValue shouldEqual Some("user not found")
  }

  it should "extract error message" in {

    val errorResponse: Response[JValue] =
      """ { "error": "user not found" } """.toResponse()

    getResponseError(errorResponse).futureValue shouldEqual "user not found"
  }
}
