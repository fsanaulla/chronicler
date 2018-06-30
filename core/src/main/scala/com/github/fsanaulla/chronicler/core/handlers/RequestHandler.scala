package com.github.fsanaulla.chronicler.core.handlers

import com.github.fsanaulla.chronicler.core.model.ImplicitRequestBuilder

/**
  * This trait define functionality for build and executing HTTP requests
 *
  * @tparam M    - Container
  * @tparam Req  - Request type
  * @tparam Resp - Response type
  */
private[chronicler] trait RequestHandler[M[_], Req, Resp, Uri]
  extends ImplicitRequestBuilder[Uri, Req] {

//  /**
//    * Build and execute HTTP request with optional body
//    * @param uri    - uri path
//    * @param entity - request body
//    * @return       - response in future container
//    */
//  def readRequest(uri: U, entity: Option[E] = None): M[R]
//
//  /**
//    * Build and execute HTTP request with body
//    * @param uri    - uri path
//    * @param entity - request body
//    * @return       - response in container
//    */
//  def writeRequest(uri: U, entity: E): M[R]

  /**
    * Execute request
    * @param request - request entity
    * @return        - Return wrapper response
    */
  def execute(request: Req): M[Resp]
}
