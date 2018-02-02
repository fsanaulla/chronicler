package com.github.fsanaulla.handlers

import com.softwaremill.sttp.{Uri, _}

private[fsanaulla] trait AsyncQueryHandler extends QueryHandler[Uri] {
  protected def buildQuery(uri: String, queryParams: Map[String, String]): Uri = uri"$uri?$queryParams"
}
