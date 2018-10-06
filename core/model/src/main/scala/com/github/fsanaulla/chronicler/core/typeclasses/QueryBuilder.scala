package com.github.fsanaulla.chronicler.core.typeclasses

import com.github.fsanaulla.chronicler.core.model.InfluxCredentials

import scala.collection.mutable

/**
  * Trait that define functionality for handling query building
  *
  * @tparam A - Result type parameter, for example for AkkaHttpBackend
  *             used `akka.http.scaladsl.model.Uri`
  */
private[chronicler] trait QueryBuilder[A] {

  /**
    * Method that build result URI object of type [A], from uri path, and query parameters
    *
    * @param uri         - string based uri path
    * @param queryParams - query parameters that will be embedded into request
    * @return            - URI object
    */
  private[chronicler] def buildQuery(uri: String, queryParams: Map[String, String]): A

  /**
    * Method that embed credentials to already created query parameters map
    *
    * @param queryMap - query parameters map
    * @return         - updated query parameters map with embedded credentials
    */
  private[chronicler] final def buildQueryParams(queryMap: mutable.Map[String, String])
                                                (implicit credentials: Option[InfluxCredentials]): Map[String, String] = {
    for {
      c <- credentials
    } yield queryMap += ("u" -> c.username, "p" -> c.password)

    queryMap.toMap
  }

  /**
    * Produce query parameters map for string parameter, with embedding credentials
    *
    * @param query - query string parameter
    * @return      - query parameters
    */
  private[chronicler] final def buildQueryParams(query: String)
                                                (implicit credentials: Option[InfluxCredentials]): Map[String, String] =
    buildQueryParams(scala.collection.mutable.Map("q" -> query))
}
