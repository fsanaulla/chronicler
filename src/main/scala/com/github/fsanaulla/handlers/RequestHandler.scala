package com.github.fsanaulla.handlers

import scala.concurrent.Future

/**
  * This trait define functionality for build and executing HTTP requests
  * @tparam R - Response type
  * @tparam U - URI type
  * @tparam M - HTTP method type
  * @tparam E - HTTP entity type
  */
trait RequestHandler[R, U, M, E] {

  protected val defaultMethod: M
  protected val defaultEntity: E

  /**
    * Build and execute HTTP request
    * @param uri - uri path
    * @param method - HTTP method
    * @param entity - request body
    * @return - response in future container
    */
  def buildRequest(uri: U, method: M = defaultMethod, entity: E = defaultEntity): Future[R]
}
