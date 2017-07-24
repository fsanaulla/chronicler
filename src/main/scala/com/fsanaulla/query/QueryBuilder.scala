package com.fsanaulla.query

import akka.http.scaladsl.model.Uri

trait QueryBuilder {

  protected def queryBuilder(path: String, queryParam: String): Uri = {
    Uri(path).withQuery(Uri.Query("q" -> queryParam))
  }

  protected def queryBuilder(path: String, queryParams: Map[String, String]): Uri = {
    Uri(path).withQuery(Uri.Query(queryParams))
  }
}
