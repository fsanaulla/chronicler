package com.github.fsanaulla.chronicler.core.handlers

import com.github.fsanaulla.chronicler.core.model.ImplicitRequestBuilder

/**
  * This trait define functionality for build and executing HTTP requests
 *
  * @tparam M    - Container
  * @tparam Req  - Request type
  * @tparam Resp - Response type
  */
private[chronicler] trait RequestHandler[M[_], Req, Resp, Uri] extends ImplicitRequestBuilder[Uri, Req] {

  /**
    * Execute request
    * @param request - request entity
    * @return        - Return wrapper response
    */
  def execute(request: Req): M[Resp]
}
