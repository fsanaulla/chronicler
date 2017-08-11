package com.fsanaulla.unit

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.stream.ActorMaterializer
import com.fsanaulla.model.{ContinuousQuery, ContinuousQueryInfo}
import com.fsanaulla.utils.ContentTypes.appJson
import com.fsanaulla.utils.Extension._
import com.fsanaulla.utils.ResponseWrapper.{toBulkJsResult, toCQResult, toSingleJsResult}
import com.fsanaulla.utils.SampleEntitys.{bulkResult, singleResult}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import spray.json.{JsArray, JsNumber, JsObject, JsString, JsonParser}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by fayaz on 12.07.17.
  */
class ResponseWrapperSpec
  extends FlatSpec
  with Matchers
  with BeforeAndAfterAll {

  implicit val actorSystem: ActorSystem = ActorSystem("TestActorSystem")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ex: ExecutionContext = actorSystem.dispatcher
  implicit val timeout: FiniteDuration = 1 second

  override def afterAll(): Unit = {
    actorSystem.terminate()
  }

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

  val bulkStrJson = """{
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
                          },
                          {
                               "statement_id": 1,
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

  val singleHttpResponse: HttpResponse = HttpResponse(entity = HttpEntity(appJson, singleStrJson))
  val bulkHttpResponse: HttpResponse = HttpResponse(entity = HttpEntity(appJson, bulkStrJson))
  val cqHttpResponse: HttpResponse = HttpResponse(entity = HttpEntity(appJson, cqStrJson))

  "single query result function" should "correctly work" in {
    toSingleJsResult(singleHttpResponse).sync shouldEqual singleResult
  }

  "bulk query result function" should "correctly work" in {
    toBulkJsResult(bulkHttpResponse).sync shouldEqual bulkResult
  }

  "cq unpacking" should "correctly work" in {
    toCQResult(cqHttpResponse).sync.filter(_.querys.nonEmpty).head shouldEqual ContinuousQueryInfo("mydb", Seq(ContinuousQuery("cq", "CREATE CONTINUOUS QUERY cq ON mydb BEGIN SELECT mean(value) AS mean_value INTO mydb.autogen.aggregate FROM mydb.autogen.cpu_load_short GROUP BY time(30m) END")))
  }
}
