package com.github.fsanaulla.core.handlers.query

import com.github.fsanaulla.core.model.HasCredentials

import scala.collection.mutable

private[core] trait QueryHandlerHelper { self: HasCredentials =>

  /**
    * Method that embed credentials to already created query parameters map
    * @param queryMap - query parameters map
    * @return - updated query parameters map with embedded credentials
    */
  def buildQueryParams(queryMap: mutable.Map[String, String]): Map[String, String] = {
    for {
      c <- credentials
    } yield queryMap += ("u" -> c.username, "p" -> c.password)

    queryMap.toMap
  }

  /**
    * Produce query parameters map for string parameter, with embedding credentials
    * @param query - query string parameter
    * @return - query parameters
    */
  def buildQueryParams(query: String): Map[String, String] = {
    buildQueryParams(scala.collection.mutable.Map("q" -> query))
  }
}
