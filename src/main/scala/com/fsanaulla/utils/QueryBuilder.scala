package com.fsanaulla.utils

import akka.http.scaladsl.model.Uri
import com.fsanaulla.model.InfluxCredentials

import scala.collection.mutable

private[fsanaulla] trait QueryBuilder {

  protected def buildQuery(path: String, queryParam: String): Uri = {
    Uri(path).withQuery(Uri.Query("q" -> queryParam))
  }

  protected def buildQuery(path: String, queryParams: Map[String, String]): Uri = {
    Uri(path).withQuery(Uri.Query(queryParams))
  }

  protected def buildQueryParams(query: String)(implicit credentials: InfluxCredentials): Map[String, String] = {
    val queryMap = scala.collection.mutable.Map("q" -> query)

    for {
      u <- credentials.username
      p <- credentials.password
    } yield queryMap += ("u" -> u, "p" -> p)

    queryMap.toMap
  }

  protected def buildQueryParams(queryMap: mutable.Map[String, String])(implicit credentials: InfluxCredentials): Map[String, String] = {
    for {
      u <- credentials.username
      p <- credentials.password
    } yield queryMap += ("u" -> u, "p" -> p)

    queryMap.toMap
  }
}
