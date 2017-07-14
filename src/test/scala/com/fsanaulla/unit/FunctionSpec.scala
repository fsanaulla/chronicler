package com.fsanaulla.unit

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.stream.ActorMaterializer
import com.fsanaulla.DatabaseHelper
import com.fsanaulla.utils.ContentTypes.appJson
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import spray.json.{JsArray, JsNumber, JsObject, JsString, JsonParser}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by fayaz on 12.07.17.
  */
class FunctionSpec
  extends FlatSpec
  with Matchers
  with DatabaseHelper
  with BeforeAndAfterAll {

  override implicit val actorSystem: ActorSystem = ActorSystem("TestActorSystem")
  override implicit val mat: ActorMaterializer = ActorMaterializer()

  override def afterAll(): Unit = {
    actorSystem.terminate()
  }

  val strJson = """{
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

  val httpResponse: HttpResponse = HttpResponse(entity = HttpEntity(appJson, strJson))

  val result: Seq[JsArray] = Seq(
    JsArray(
      JsString("2015-01-29T21:55:43.702900257Z"),
      JsNumber(2)),
    JsArray(
      JsString("2015-01-29T21:55:43.702900257Z"),
      JsNumber(0.55)),
    JsArray(
      JsString("2015-06-11T20:46:02Z"),
      JsNumber(0.64))
  )

  "toJson function" should "correctly work" in {
    Await.result(toJson(httpResponse).map(seq => seq shouldEqual result), 1 second)
  }
}
