package com.github.fsanaulla.unit

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.stream.ActorMaterializer
import com.github.fsanaulla.model.InfluxImplicits._
import com.github.fsanaulla.model.{ContinuousQuery, ContinuousQueryInfo}
import com.github.fsanaulla.utils.ContentTypes.AppJson
import com.github.fsanaulla.utils.SampleEntitys.singleResult
import com.github.fsanaulla.utils.{AkkaResponseHandler, TestSpec}
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by fayaz on 12.07.17.
  */
class ResponseHandlerSpec
  extends TestSpec
    with BeforeAndAfterAll
    with AkkaResponseHandler {

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

  val singleHttpResponse: HttpResponse = HttpResponse(entity = HttpEntity(AppJson, singleStrJson))
  val cqHttpResponse: HttpResponse = HttpResponse(entity = HttpEntity(AppJson, cqStrJson))
  val errHttpResponse = HttpResponse(entity = HttpEntity(AppJson, errJson))

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
