package com.github.fsanaulla.chronicler.akka.handlers

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import _root_.akka.stream.ActorMaterializer
import com.github.fsanaulla.chronicler.akka.utils.AkkaContentTypes._
import com.github.fsanaulla.chronicler.akka.utils.SampleEntitys.singleResult
import com.github.fsanaulla.core.model.{ContinuousQuery, ContinuousQueryInfo}
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.core.utils.InfluxImplicits._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by fayaz on 12.07.17.
  */
class AkkaResponseHandlerSpec
  extends TestSpec
    with AkkaResponseHandler {

  implicit val actorSystem: ActorSystem = ActorSystem("TestActorSystem")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ex: ExecutionContext = actorSystem.dispatcher
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

  val cqStr = """{
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

  val errStr =
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

  val singleHttpResponse: HttpResponse = HttpResponse(entity = HttpEntity(AppJson, singleStrJson))
  val cqHttpResponse: HttpResponse = HttpResponse(entity = HttpEntity(AppJson, cqStr))
  val errHttpResponse = HttpResponse(entity = HttpEntity(AppJson, errStr))

  "Response handler" should "extract query result" in {
    toQueryJsResult(singleHttpResponse).futureValue.queryResult shouldEqual singleResult
  }

  it should "extract CQ information result" in {
    toCqQueryResult(cqHttpResponse).futureValue.queryResult.filter(_.querys.nonEmpty).head shouldEqual ContinuousQueryInfo("mydb", Seq(ContinuousQuery("cq", "CREATE CONTINUOUS QUERY cq ON mydb BEGIN SELECT mean(value) AS mean_value INTO mydb.autogen.aggregate FROM mydb.autogen.cpu_load_short GROUP BY time(30m) END")))
  }

  it should "extract error message" in {
    getErrorOpt(errHttpResponse).futureValue shouldEqual Some("user not found")
  }
}
