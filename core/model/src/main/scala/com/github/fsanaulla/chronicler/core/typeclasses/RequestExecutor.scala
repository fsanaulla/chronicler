package com.github.fsanaulla.chronicler.core.typeclasses

/**
  * This trait define functionality for build and executing HTTP requests
  *
  * @tparam F - Container
  * @tparam Req - Request type
  * @tparam Resp - Response type
  */
private[chronicler] trait RequestExecutor[F[_], Req, Resp, Uri] {

  /**
    * Implicit conversion from Uri to Request, provided to reduce boilerplate
    *
    * @param uri - Uri parameter
    * @return    - request entity
    */
  private[chronicler] implicit def buildRequest(uri: Uri): Req

  /**
    * Execute request
    *
    * @param request - request entity
    * @return        - Return wrapper response
    */
  private[chronicler] def execute(request: Req): F[Resp]
}
