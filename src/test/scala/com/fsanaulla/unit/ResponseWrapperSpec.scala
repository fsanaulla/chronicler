package com.fsanaulla.unit

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.stream.ActorMaterializer
import com.fsanaulla.utils.ContentTypes.appJson
import com.fsanaulla.utils.Helper._
import com.fsanaulla.utils.ResponseWrapper
import com.fsanaulla.utils.SampleEntitys.{bulkResult, singleResult}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import spray.json.{JsArray, JsNumber, JsObject, JsString, JsonParser}

import scala.concurrent.ExecutionContext

/**
  * Created by fayaz on 12.07.17.
  */
class ResponseWrapperSpec
  extends FlatSpec
  with Matchers
  with BeforeAndAfterAll
  with ResponseWrapper {

  implicit val actorSystem: ActorSystem = ActorSystem("TestActorSystem")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ex: ExecutionContext = actorSystem.dispatcher

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

  val singleHttpResponse: HttpResponse = HttpResponse(entity = HttpEntity(appJson, singleStrJson))
  val bulkHttpResponse: HttpResponse = HttpResponse(entity = HttpEntity(appJson, bulkStrJson))

  "single query result function" should "correctly work" in {
    await(toSingleJsResult(singleHttpResponse)) shouldEqual singleResult
  }

  "bulk query result function" should "correctly work" in {
    await(toBulkJsResult(bulkHttpResponse)) shouldEqual bulkResult
  }
}
