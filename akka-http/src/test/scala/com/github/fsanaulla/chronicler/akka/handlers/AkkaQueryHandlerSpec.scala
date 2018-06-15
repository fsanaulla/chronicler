package com.github.fsanaulla.chronicler.akka.handlers

import com.github.fsanaulla.chronicler.core.model.{HasCredentials, InfluxCredentials}
import com.github.fsanaulla.chronicler.testing.TestSpec

class AkkaQueryHandlerSpec
  extends TestSpec
    with AkkaQueryHandler
    with HasCredentials {

  override val credentials: Option[InfluxCredentials] = None

  "Query handler" should "properly generate URI" in {
    val queryMap = scala.collection.mutable.Map[String, String](
      "q" -> "FirstQuery;SecondQuery"
    )
    val res = s"/query?q=FirstQuery%3BSecondQuery"

    buildQuery("/query", buildQueryParams(queryMap)).toString() shouldEqual res
  }

}
