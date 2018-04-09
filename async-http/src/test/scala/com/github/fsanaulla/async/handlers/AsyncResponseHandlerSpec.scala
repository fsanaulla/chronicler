package com.github.fsanaulla.async.handlers

import com.github.fsanaulla.async.utils.Extensions.RichTry
import com.github.fsanaulla.async.utils.SampleEntitys.singleResult
import com.github.fsanaulla.chronicler.async.handlers.AsyncResponseHandler
import com.github.fsanaulla.core.model.ContinuousQuery
import com.github.fsanaulla.core.test.TestSpec
import com.github.fsanaulla.core.utils.DefaultInfluxImplicits._
import com.softwaremill.sttp.Response
import jawn.ast.JParser

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class AsyncResponseHandlerSpec extends TestSpec with AsyncResponseHandler {

  protected implicit val ex: ExecutionContext = ExecutionContext.Implicits.global

  implicit val timeout: FiniteDuration = 1 second

  val p: JParser.type = JParser

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



  val singleHttpResponse = Response(body = p.parseFromString(singleStrJson).toStrEither(singleStrJson), 200, "", Nil, Nil)

  "AsyncResponseHandler" should "extract single result from response" in {
    toQueryJsResult(singleHttpResponse).futureValue.queryResult shouldEqual singleResult
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

  it should "extract error message" in {

    val errJson =
      """{
        "results": [
          {
            "statement_id": 0,
            "error": "user not found"
          }
        ]
      }"""

    val errHttpResponse = Response(p.parseFromString(errJson).toStrEither(errJson), 200, "", Nil, Nil)


    getErrorOpt(errHttpResponse).futureValue shouldEqual Some("user not found")
  }
}
