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

package com.github.fsanaulla.chronicler.async.management

import java.nio.ByteBuffer

import com.github.fsanaulla.chronicler.core.components.ResponseHandlerBase
import com.github.fsanaulla.chronicler.core.implicits._
import com.github.fsanaulla.chronicler.core.management.cq.ContinuousQuery
import com.github.fsanaulla.chronicler.core.management.ManagementResponseHandler
import org.scalatest.EitherValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Second, Seconds, Span}
import com.github.fsanaulla.chronicler.testing.getJsonStringFromFile
import org.typelevel.jawn.ast._
import com.github.fsanaulla.chronicler.async.shared._
import sttp.client3

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import com.github.fsanaulla.chronicler.async.shared.AsyncJsonHandler
import scala.io.Source

/** Created by Author: fayaz.sanaulla@gmail.com Date: 10.08.17
  */
class AsyncResponseHandlerSpec
    extends AnyFlatSpec
    with Matchers
    with ScalaFutures
    with EitherValues {

  val jsonHandler                 = new AsyncJsonHandler
  implicit val pc: PatienceConfig = PatienceConfig(Span(20, Seconds), Span(1, Second))

  implicit val ex: ExecutionContext = ExecutionContext.Implicits.global

  implicit val timeout: FiniteDuration = 1.second

  implicit val p: JParser.type = JParser

  val rh = new ManagementResponseHandler(jsonHandler)

  def mkResponse(body: String): ResponseE =
    client3.Response.ok(Right(body))

  it should "extract single query queryResult from response" in {

    val singleHttpResponse =
      mkResponse(getJsonStringFromFile("/single-response.json"))

    val result = Array(
      JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(2))),
      JArray(Array(JString("2015-01-29T21:55:43.702900257Z"), JNum(0.55))),
      JArray(Array(JString("2015-06-11T20:46:02Z"), JNum(0.64)))
    )

    rh.queryResultJson(singleHttpResponse).value shouldEqual result
  }

  it should "extract bulk query results from response" in {

    val bulkHttpResponse = mkResponse(getJsonStringFromFile("/bulk-response.json"))

    rh.bulkQueryResultJson(bulkHttpResponse).value shouldEqual Array(
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

  it should "cq unpacking" in {

    val cqResponse = mkResponse(getJsonStringFromFile("/cq.json"))

    val cqi = rh.toCqQueryResult(cqResponse).value.filter(_.queries.nonEmpty).head
    cqi.dbName shouldEqual "mydb"
    cqi.queries.head shouldEqual ContinuousQuery(
      "cq",
      "CREATE CONTINUOUS QUERY cq ON mydb BEGIN SELECT mean(value) AS mean_value INTO mydb.autogen.aggregate FROM mydb.autogen.cpu_load_short GROUP BY time(30m) END"
    )
  }

  it should "extract optional error message" in {

    val errorHttpResponse = mkResponse(getJsonStringFromFile("/error.json"))

    jsonHandler.responseErrorMsgOpt(errorHttpResponse).value shouldEqual Some(
      "user not found"
    )
  }

  it should "extract error message" in {

    val errorHttpResponse = mkResponse(getJsonStringFromFile("/err-msg.json"))

    jsonHandler.responseErrorMsg(errorHttpResponse) shouldEqual Right("user not found")
  }
}
