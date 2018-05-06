package com.github.fsanaulla.chronicler.akka.handlers

import com.github.fsanaulla.core.test.{EmptyCredentials, TestSpec}

class AkkaQueryHandlerSpec extends TestSpec with EmptyCredentials with AkkaQueryHandler {

  "Query handler" should "properly generate URI" in {
    val queryMap = scala.collection.mutable.Map[String, String](
      "q" -> "FirstQuery;SecondQuery"
    )
    val res = s"/query?q=FirstQuery%3BSecondQuery"

    buildQuery("/query", buildQueryParams(queryMap)).toString() shouldEqual res
  }

}
