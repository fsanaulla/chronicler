/*
 * Copyright 2017-2019 Faiaz Sanaulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fsanaulla.chronicler.akka.shared.handlers

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import com.github.fsanaulla.chronicler.akka.shared.{ResponseE}
import akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.shared.AkkaJsonHandler
import org.scalatest._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import com.github.fsanaulla.chronicler.testing.{BaseSpec, getJsonStringFromFile}
import org.typelevel.jawn.ast._
import sttp.client3

import scala.concurrent.ExecutionContextExecutor

class AkkaJsonHandlerSpec
    extends TestKit(ActorSystem())
    with BaseSpec
    with EitherValues
    with OptionValues
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    super.afterAll()
    mat.shutdown()
    TestKit.shutdownActorSystem(system)
  }

  def mkResponse(body: String): ResponseE =
    client3.Response.ok(Right(body))

  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val mat: ActorMaterializer       = ActorMaterializer()

  val jsonHandler = new AkkaJsonHandler()

  "JsonHandler" - {

    "should" - {

      "extract" - {

        "body from HTTP response" in {
          val singleStrJson = getJsonStringFromFile("/json/single.json")
          val resp          = mkResponse(singleStrJson)

          val result: JValue = JParser.parseFromString(singleStrJson).get

          jsonHandler.responseBody(resp).value shouldEqual result
        }

        "query result from JSON" in {
          val queryResult = JParser.parseUnsafe(getJsonStringFromFile("/json/query.json"))

          val result = Array(
            JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JString("Fz"), JNum(2))),
            JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JString("Rz"), JNum(0.55))),
            JArray(Array(JString("2015-06-11T20:46:02Z"), JNull, JNum(0.64)))
          )

          jsonHandler.queryResult(queryResult).value shouldEqual result
        }

        "empty query result from JSON" in {
          val json = JParser.parseUnsafe(getJsonStringFromFile("/json/query-empty.json"))

          jsonHandler.queryResult(json) shouldEqual None
        }

        "bulk" - {

          "query result from JSON" in {
            val json =
              JParser.parseUnsafe(getJsonStringFromFile("/json/query-bulk.json"))

            val result = Array(
              Array(
                JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
                JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
                JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))
              ),
              Array(
                JArray(Array(JString("1970-01-01T00:00:00Z"), JNum(3)))
              )
            )

            jsonHandler.bulkResult(json).value shouldEqual result
          }

          "partially empty query result from JSON" in {
            val json =
              JParser.parseUnsafe(getJsonStringFromFile("/json/query-bulk-partially.json"))

            val result = Array(
              Array(
                JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
                JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
                JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))
              )
            )

            jsonHandler.bulkResult(json).value shouldEqual result
          }

          "empty query result from JSON" in {
            val json = JParser.parseUnsafe(getJsonStringFromFile("/json/query-empty.json"))

            jsonHandler.bulkResult(json).value shouldEqual Array.empty[Array[JArray]]
          }
        }

        "grouped" - {

          "system query result from JSON" in {
            val json =
              JParser.parseUnsafe(getJsonStringFromFile("/json/grouped-system.json"))

            val result = Array(
              "cpu_load_short" -> Array(
                JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
                JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
                JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))
              )
            )

            val res = jsonHandler.groupedSystemInfoJs(json).value

            res.length shouldEqual 1
            val (measurament, points) = res.head

            measurament shouldEqual "cpu_load_short"
            points shouldEqual result.head._2
          }

          "query result from JSON" in {
            val json          = JParser.parseUnsafe(getJsonStringFromFile("/json/grouped.json"))
            val groupedResult = jsonHandler.groupedResult(json)

            groupedResult should not be None

            val result = groupedResult.get
            result.length shouldEqual 2

            result.map { case (k, v) => k.toList -> v.toList }.toList shouldEqual List(
              List("server01", "us-west") -> List(
                JArray(Array(JString("1970-01-01T00:00:00Z"), JNum(0.69)))
              ),
              List("server02", "us-west") -> List(
                JArray(Array(JString("1970-01-01T00:00:00Z"), JNum(0.73)))
              )
            )
          }
        }
      }
    }
  }
}
