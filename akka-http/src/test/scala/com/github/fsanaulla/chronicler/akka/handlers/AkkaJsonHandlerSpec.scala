package com.github.fsanaulla.chronicler.akka.handlers

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import _root_.akka.stream.ActorMaterializer
import com.github.fsanaulla.chronicler.akka.utils.AkkaContentTypes._
import com.github.fsanaulla.core.test.TestSpec
import jawn.ast.JParser

import scala.concurrent.ExecutionContext

class AkkaJsonHandlerSpec extends TestSpec with AkkaJsonHandler {

  implicit val system: ActorSystem = ActorSystem("TestOne")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ex: ExecutionContext = system.dispatcher

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

  val singleHttpResponse: HttpResponse = HttpResponse(entity = HttpEntity(AppJson, singleStrJson))

  val jsResult = JParser.parseFromString(singleStrJson).toOption.value

  "Akka json handler" should "extract js object from HTTP response" in {
    getJsBody(singleHttpResponse).futureValue shouldEqual jsResult
  }
}
