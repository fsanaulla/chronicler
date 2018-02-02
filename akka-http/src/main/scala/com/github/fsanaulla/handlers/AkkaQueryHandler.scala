package com.github.fsanaulla.handlers

import akka.http.scaladsl.model.Uri

private[fsanaulla] trait AkkaQueryHandler extends QueryHandler[Uri] {

  protected def buildQuery(uri: String, queryParams: Map[String, String]): Uri = {
    Uri(uri).withQuery(Uri.Query(queryParams))
  }
}
