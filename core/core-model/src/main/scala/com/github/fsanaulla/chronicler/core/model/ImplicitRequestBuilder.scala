package com.github.fsanaulla.chronicler.core.model

/**
  * Request entity builder from uri parameter, in case of simply read reqeust to Influx
  * @tparam Uri     - Http request uri type
  * @tparam Request - Http request type projection
  */
trait ImplicitRequestBuilder[Uri, Request] {

  /**
    * Implicit conversion from Uri to Request,
    * provided to reduce boilerplate
    * @param uri - Uri parameter
    * @return    - request entity
    */
  private[chronicler] implicit def req(uri: Uri): Request
}
