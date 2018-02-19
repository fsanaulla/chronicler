package com.github.fsanaulla.handlers

import com.github.fsanaulla.core.model.InfluxImplicits._
import com.github.fsanaulla.core.model.{ContinuousQuery, ContinuousQueryInfo}
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.utils.SampleEntitys.singleResult
import com.softwaremill.sttp.Response
import spray.json.JsonParser

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class AsyncResponseHandlerSpec
  extends TestSpec
    with AsyncResponseHandler {

  protected val ex: ExecutionContext = ExecutionContext.Implicits.global

  implicit val timeout: FiniteDuration = 1 second

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

  val errJson =
    """
    {
      "results": [
        {
          "statement_id": 0,
          "error": "user not found"
        }
      ]
    }
  """

  val singleHttpResponse = Response(body = Right(JsonParser(singleStrJson).asJsObject), 200, "", Nil, Nil)
  val cqHttpResponse = Response(Right(JsonParser(cqStrJson).asJsObject), 200, "", Nil, Nil)
  val errHttpResponse = Response(Right(JsonParser(errJson).asJsObject), 200, "", Nil, Nil)

  "single query result function" should "correctly work" in {
    toQueryJsResult(singleHttpResponse).futureValue.queryResult shouldEqual singleResult
  }

  "cq unpacking" should "correctly work" in {
    toCqQueryResult(cqHttpResponse).futureValue.queryResult.filter(_.querys.nonEmpty).head shouldEqual ContinuousQueryInfo("mydb", Seq(ContinuousQuery("cq", "CREATE CONTINUOUS QUERY cq ON mydb BEGIN SELECT mean(value) AS mean_value INTO mydb.autogen.aggregate FROM mydb.autogen.cpu_load_short GROUP BY time(30m) END")))
  }

  "optError handler" should "correct work" in {

    getErrorOpt(errHttpResponse).futureValue shouldEqual Some("user not found")
  }
}
