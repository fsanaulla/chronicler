package com.github.fsanaulla.handlers

import com.github.fsanaulla.core.handlers.QueryHandler
import com.softwaremill.sttp.{Uri, _}

private[fsanaulla] trait AsyncQueryHandler extends QueryHandler[Uri] {
  protected val host: String
  protected val port: Int
  override def buildQuery(uri: String, queryParams: Map[String, String]): Uri = uri"http://$host:$port$uri?$queryParams"
}
