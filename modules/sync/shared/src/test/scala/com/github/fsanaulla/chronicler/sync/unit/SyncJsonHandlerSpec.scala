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

package com.github.fsanaulla.chronicler.sync.unit

import com.github.fsanaulla.chronicler.sync.shared.SyncJsonHandler
import com.github.fsanaulla.chronicler.testing.BaseSpec
import com.github.fsanaulla.chronicler.testing._
import org.scalatest.{EitherValues, OptionValues, TryValues}
import org.typelevel.jawn.ast._
import sttp.client3.Response

/** Created by Author: fayaz.sanaulla@gmail.com Date: 10.08.17
  */
class SyncJsonHandlerSpec extends BaseSpec with TryValues with EitherValues with OptionValues {

  "Json handler" - {

    val jh = new SyncJsonHandler

    "should extract from json" - {

      "body" in {
        val singleStrJson                           = getJsonStringFromFile("/json/single.json")
        val input: Response[Either[String, String]] = mkResponse(singleStrJson)
        val out: JValue                             = JParser.parseFromString(singleStrJson).get

        jh.responseBody(input).success.value.value shouldEqual out
      }

      "query result" in {
        val json = JParser.parseUnsafe(getJsonStringFromFile("/json/query.json"))

        val out = Array(
          JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JString("Fz"), JNum(2))),
          JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JString("Rz"), JNum(0.55))),
          JArray(Array(JString("2015-06-11T20:46:02Z"), JNull, JNum(0.64)))
        )

        jh.queryResult(json).value shouldEqual out
      }

      "empty query result" in {
        val json = JParser.parseUnsafe(getJsonStringFromFile("/json/query-empty.json"))

        jh.queryResult(json) shouldEqual None
      }

      "bulk query result" in {
        val json =
          JParser.parseUnsafe(getJsonStringFromFile("/json/query-bulk.json"))

        val out = Array(
          Array(
            JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
            JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
            JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))
          ),
          Array(
            JArray(Array(JString("1970-01-01T00:00:00Z"), JNum(3)))
          )
        )

        jh.bulkResult(json).value shouldEqual out
      }

      "partially empty bulk query result" in {
        val json =
          JParser.parseUnsafe(getJsonStringFromFile("/json/query-bulk-partially.json"))

        val out = Array(
          Array(
            JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
            JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
            JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))
          )
        )

        jh.bulkResult(json).get shouldEqual out
      }

      "empty bulk query result" in {
        val json = JParser.parseUnsafe(getJsonStringFromFile("/json/query-bulk-empty.json"))

        jh.bulkResult(json).get shouldEqual Array.empty[Array[JArray]]
      }

      "grouped system query result" in {
        val json = JParser.parseUnsafe(getJsonStringFromFile("/json/grouped.json"))
        val result = Array(
          "cpu_load_short" -> Array(
            JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
            JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
            JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))
          )
        )

        val groupedSystemQuery = jh.groupedSystemInfoJs(json).value

        groupedSystemQuery.length shouldEqual 1

        val (measName, points) = groupedSystemQuery.headOption.value
        measName shouldEqual "cpu_load_short"
        points shouldEqual result.head._2
      }

      "grouped query result" in {
        val json          = JParser.parseUnsafe(getJsonStringFromFile("/json/grouped-system.json"))
        val groupedResult = jh.groupedResult(json).value

        groupedResult.length shouldEqual 2
        groupedResult.map { case (k, v) => k.toList -> v.toList }.toList shouldEqual List(
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
