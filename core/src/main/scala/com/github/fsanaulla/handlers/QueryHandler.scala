package com.github.fsanaulla.handlers

import com.github.fsanaulla.model.InfluxCredentials

import scala.collection.mutable

/**
  * Trait that define functionality for handling query building
  * @tparam A - Result type parameter, for example for AkkaHttpBackend
  *             used `akka.http.scaladsl.model.Uri`
  */
trait QueryHandler[A] {

  /**
    * Method that build result URI object of type [A], from uri path, and query parameters
    * @param uri - string based uri path
    * @param queryParams - query parameters that will be embedded into request
    * @return - resulr URI object
    */
  protected def buildQuery(uri: String, queryParams: Map[String, String]): A

  /**
    * Method that embed credentials to already created query parameters map
    * @param queryMap - query parameters map
    * @param credentials - implicit credentials
    * @return - updated query parameters map with embedded credentials
    */
  protected def buildQueryParams(queryMap: mutable.Map[String, String])(implicit credentials: InfluxCredentials): Map[String, String]

  /**
    * Produce query parameters map for string parameter, with embedding credentials
    * @param query - query string parameter
    * @param credentials - implicit user's credentials
    * @return - query parameters
    */
  protected def buildQueryParams(query: String)(implicit credentials: InfluxCredentials): Map[String, String]
}
