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

package com.github.fsanaulla.chronicler.urlhttp.shared.handlers

import com.github.fsanaulla.chronicler.core.components.ResponseHandler
import com.github.fsanaulla.chronicler.core.implicits._
import com.github.fsanaulla.chronicler.core.model.ContinuousQuery
import com.github.fsanaulla.chronicler.testing.BaseSpec
import com.github.fsanaulla.chronicler.urlhttp.shared.UrlJsonHandler
import com.github.fsanaulla.chronicler.urlhttp.shared._
import org.scalatest.EitherValues
import org.scalatest.OptionValues
import org.scalatest.TryValues
import org.typelevel.jawn.ast._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class UrlResponseHandlerSpec extends BaseSpec with TryValues with EitherValues with OptionValues {

  "Response handler" - {
    val respHandler = new ResponseHandler(UrlJsonHandler)

    "should extract from response" - {

      "single query result" in {
        val singleResponse = mkResponse(getJsonStringFromFile("/response/single-response.json"))

        val result = Array(
          JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
          JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
          JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))
        )

        respHandler.queryResultJson(singleResponse).success.value.value shouldEqual result
      }

      "bulk query results" in {
        val bulkResponse = mkResponse(getJsonStringFromFile("/response/bulk-response.json"))

        respHandler.bulkQueryResultJson(bulkResponse).success.value.value shouldEqual Array(
          Array(
            JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
            JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
            JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))
          ),
          Array(
            JArray(Array(JString("1970-01-01T00:00:00Z"), JNum(3)))
          )
        )
      }

      "continues query result" in {

        val cqStrJson = mkResponse(getJsonStringFromFile("/response/cq.json"))

        val cqi =
          respHandler
            .toCqQueryResult(cqStrJson)
            .success
            .value
            .value
            .find(_.queries.nonEmpty)
            .value

        cqi.dbName shouldEqual "mydb"
        cqi.queries.head shouldEqual ContinuousQuery(
          "cq",
          "CREATE CONTINUOUS QUERY cq ON mydb BEGIN SELECT mean(value) AS mean_value INTO mydb.autogen.aggregate FROM mydb.autogen.cpu_load_short GROUP BY time(30m) END"
        )
      }

      "optional error message" in {
        val errorResponse = mkResponse(getJsonStringFromFile("/response/error.json"))

        UrlJsonHandler
          .responseErrorMsgOpt(errorResponse)
          .success
          .value
          .value
          .value shouldEqual "user not found"
      }

      "error message" in {
        val errorResponse = mkResponse(getJsonStringFromFile("/response/err-msg.json"))

        UrlJsonHandler
          .responseErrorMsg(errorResponse)
          .success
          .value
          .value shouldEqual "user not found"
      }
    }
  }
}
