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

package com.github.fsanaulla.chronicler.akka.query

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.shared.AkkaJsonHandler
import com.github.fsanaulla.chronicler.akka.shared.ResponseE
import com.github.fsanaulla.chronicler.core.components.ResponseHandlerBase
import com.github.fsanaulla.chronicler.core.management.ManagementResponseHandler
import com.github.fsanaulla.chronicler.core.management.cq.ContinuousQuery
import com.github.fsanaulla.chronicler.testing.getJsonStringFromFile
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, EitherValues}
import org.typelevel.jawn.ast._
import sttp.client3.Response

import scala.concurrent.ExecutionContextExecutor
import scala.io.Source

/** Created by fayaz on 12.07.17.
  */
class AkkaResponseHandlerSpec
    extends TestKit(ActorSystem())
    with AnyFlatSpecLike
    with ScalaFutures
    with IntegrationPatience
    with Matchers
    with EitherValues
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    super.afterAll()
    mat.shutdown()
    TestKit.shutdownActorSystem(system)
  }

  def mkResponse(str: String): ResponseE = Response.ok(Right(str))

  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val mat: ActorMaterializer       = ActorMaterializer()
  val jsonHandler                           = new AkkaJsonHandler()
  val rh                                    = new ManagementResponseHandler(jsonHandler)

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
