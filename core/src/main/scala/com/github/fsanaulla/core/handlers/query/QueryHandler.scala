package com.github.fsanaulla.core.handlers.query

import com.github.fsanaulla.core.model.HasCredentials

/**
  * Trait that define functionality for handling query building
  * @tparam A - Result type parameter, for example for AkkaHttpBackend
  *             used `akka.http.scaladsl.model.Uri`
  */
private[fsanaulla] trait QueryHandler[A] extends QueryHandlerHelper { self: HasCredentials =>

  /**
    * Method that build result URI object of type [A], from uri path, and query parameters
    * @param uri - string based uri path
    * @param queryParams - query parameters that will be embedded into request
    * @return - resulr URI object
    */
  def buildQuery(uri: String, queryParams: Map[String, String]): A
}
