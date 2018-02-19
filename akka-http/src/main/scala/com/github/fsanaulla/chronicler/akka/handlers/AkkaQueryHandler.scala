package com.github.fsanaulla.chronicler.akka.handlers

import _root_.akka.http.scaladsl.model.Uri
import com.github.fsanaulla.core.handlers.QueryHandler

private[fsanaulla] trait AkkaQueryHandler extends QueryHandler[Uri] {

  override def buildQuery(uri: String, queryParams: Map[String, String]): Uri =
    Uri(uri).withQuery(Uri.Query(queryParams))
}
