package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.chronicler.async.utils.Extensions.RichUri
import com.github.fsanaulla.core.handlers.QueryHandler
import com.github.fsanaulla.core.model.HasCredentials
import com.softwaremill.sttp._

private[fsanaulla] trait AsyncQueryHandler
  extends QueryHandler[Uri]
    with HasCredentials {

  protected val host: String
  protected val port: Int

  override def buildQuery(uri: String, queryParams: Map[String, String]): Uri = {
    val u = Uri(host, port).path(uri).optParams(queryParams)
    val q0 = u.paramsMap("q")
    val q1 = q0.replace(";", "%3B")

    u
  }
}
