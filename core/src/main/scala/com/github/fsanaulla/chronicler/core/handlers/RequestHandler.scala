package com.github.fsanaulla.chronicler.core.handlers

/**
  * This trait define functionality for build and executing HTTP requests
  * @tparam M - Container
  * @tparam R - Response type
  * @tparam U - URI type
  * @tparam E - HTTP entity type
  */
private[chronicler] trait RequestHandler[M[_], R, U, E] {

  /**
    * Build and execute HTTP request with optional body
    * @param uri    - uri path
    * @param entity - request body
    * @return       - response in future container
    */
  def readRequest(uri: U, entity: Option[E] = None): M[R]

  /**
    * Build and execute HTTP request with body
    * @param uri    - uri path
    * @param entity - request body
    * @return       - response in container
    */
  def writeRequest(uri: U, entity: E): M[R]
}
