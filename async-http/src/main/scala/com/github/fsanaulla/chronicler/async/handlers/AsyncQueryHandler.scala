package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.chronicler.async.utils.Extensions.RichUri
import com.github.fsanaulla.core.handlers.QueryHandler
import com.softwaremill.sttp.Uri

private[fsanaulla] trait AsyncQueryHandler extends QueryHandler[Uri] {
  protected val host: String
  protected val port: Int

  override def buildQuery(uri: String, queryParams: Map[String, String]): Uri = {
    Uri(host = host, port).path(uri).optParams(queryParams)
  }
}
