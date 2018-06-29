package com.github.fsanaulla.chronicler.core.model

/**
  * Request entity builder from uri parameter, in case of simply read reqeust to Influx
  * @tparam U - Http request uri type
  * @tparam R - Http request type projection
  */
trait RequestBuilder[U, R] {

  /**
    * Implicit conversion from Uri to Request,
    * provided to reduce boilerplate
    * @param uri - Uri parameter
    * @return    - request entity
    */
  implicit def req(uri: U): R
}
