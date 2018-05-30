package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.chronicler.testing.{EmptyCredentials, TestSpec}

class AsyncQueryHandlerSpec
  extends TestSpec
    with EmptyCredentials
    with AsyncQueryHandler {

  val host = "localhost"
  val port = 8080

  "Query handler" should "properly generate URI" in {
    val queryMap = scala.collection.mutable.Map[String, String](
      "q" -> "FirstQuery;SecondQuery"
    )
    val res = s"http://$host:$port/query?q=FirstQuery%3BSecondQuery"

    buildQuery("/query", buildQueryParams(queryMap)).toString() shouldEqual res
  }

}
