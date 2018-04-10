package com.github.fsanaulla.chronicler.akka.handlers

import _root_.akka.http.scaladsl.model.Uri
import com.github.fsanaulla.core.handlers.query.QueryHandler
import com.github.fsanaulla.core.model.HasCredentials

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[fsanaulla] trait AkkaQueryHandler
  extends QueryHandler[Uri]
    with HasCredentials {

  override def buildQuery(uri: String, queryParams: Map[String, String]): Uri =
    Uri(uri).withQuery(Uri.Query(queryParams))
}
