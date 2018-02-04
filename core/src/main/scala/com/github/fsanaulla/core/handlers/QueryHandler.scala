package com.github.fsanaulla.core.handlers

import com.github.fsanaulla.core.model.InfluxCredentials

import scala.collection.mutable

/**
  * Trait that define functionality for handling query building
  * @tparam A - Result type parameter, for example for AkkaHttpBackend
  *             used `akka.http.scaladsl.model.Uri`
  */
private[fsanaulla] trait QueryHandler[A] {

  /**
    * Method that build result URI object of type [A], from uri path, and query parameters
    * @param uri - string based uri path
    * @param queryParams - query parameters that will be embedded into request
    * @return - resulr URI object
    */
  def buildQuery(uri: String, queryParams: Map[String, String]): A

  /**
    * Method that embed credentials to already created query parameters map
    * @param queryMap - query parameters map
    * @param credentials - implicit credentials
    * @return - updated query parameters map with embedded credentials
    */
  def buildQueryParams(queryMap: mutable.Map[String, String])(implicit credentials: InfluxCredentials): Map[String, String] = {
    for {
      u <- credentials.username
      p <- credentials.password
    } yield queryMap += ("u" -> u, "p" -> p)

    queryMap.toMap
  }

  /**
    * Produce query parameters map for string parameter, with embedding credentials
    * @param query - query string parameter
    * @param credentials - implicit user's credentials
    * @return - query parameters
    */
  def buildQueryParams(query: String)(implicit credentials: InfluxCredentials): Map[String, String] = {
    buildQueryParams(scala.collection.mutable.Map("q" -> query))
  }
}
