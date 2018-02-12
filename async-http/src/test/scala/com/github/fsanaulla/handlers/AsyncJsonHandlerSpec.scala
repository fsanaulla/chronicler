package com.github.fsanaulla.handlers

import com.github.fsanaulla.TestSpec
import com.softwaremill.sttp.Response
import spray.json.{JsObject, JsonParser}

class AsyncJsonHandlerSpec extends TestSpec with AsyncJsonHandler {

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

  val singleHttpResponse = Response(body = Right(JsonParser(singleStrJson).asJsObject), 200, "", Nil, Nil)

  val jsResult: JsObject = JsonParser(singleStrJson).asJsObject

  "Akka json handler" should "extract js object from HTTP response" in {
    getJsBody(singleHttpResponse).futureValue shouldEqual jsResult
  }
}
