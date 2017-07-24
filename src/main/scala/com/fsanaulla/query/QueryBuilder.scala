package com.fsanaulla.query

import akka.http.scaladsl.model.Uri

trait QueryBuilder {
  protected def queryBuilder(queryParam: String): Uri = {
    Uri("/query").withQuery(Uri.Query("q" -> queryParam))
  }
}
