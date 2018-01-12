package com.github.fsanaulla.utils

import akka.http.scaladsl.model.Uri
import com.github.fsanaulla.handlers.QueryHandler
import com.github.fsanaulla.model.InfluxCredentials

import scala.collection.mutable

private[fsanaulla] trait AkkaQueryHandler extends QueryHandler[Uri]{

  protected def buildQuery(uri: String, queryParams: Map[String, String]): Uri = {
    Uri(uri).withQuery(Uri.Query(queryParams))
  }

  protected def buildQueryParams(query: String)(implicit credentials: InfluxCredentials): Map[String, String] = {
    buildQueryParams(scala.collection.mutable.Map("q" -> query))
  }

  protected def buildQueryParams(queryMap: mutable.Map[String, String])(implicit credentials: InfluxCredentials): Map[String, String] = {
    for {
      u <- credentials.username
      p <- credentials.password
    } yield queryMap += ("u" -> u, "p" -> p)

    queryMap.toMap
  }
}
