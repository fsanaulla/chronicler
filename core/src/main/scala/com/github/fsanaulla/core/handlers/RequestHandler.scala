package com.github.fsanaulla.core.handlers

import scala.concurrent.Future

/**
  * This trait define functionality for build and executing HTTP requests
  * @tparam R - Response type
  * @tparam U - URI type
  * @tparam M - HTTP method type
  * @tparam E - HTTP entity type
  */
private[fsanaulla] trait RequestHandler[R, U, M, E] {

  protected val defaultMethod: M
  /**
    * Build and execute HTTP request
    * @param uri - uri path
    * @param method - HTTP method
    * @param entity - request body
    * @return - response in future container
    */
  def readRequest(uri: U, method: M = defaultMethod, entity: Option[E] = None): Future[R]

  def writeRequest(uri: U, method: M = defaultMethod, entity: E): Future[R]
}
