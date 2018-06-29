package com.github.fsanaulla.chronicler.akka.utils

import akka.http.scaladsl.model.headers.{HttpEncodings, `Accept-Encoding`}

/** All headers required during client lifecycle */
private[akka] object AkkaHeaders {

  /** Header for GZIP content encoding */
  val gzipEncoding = `Accept-Encoding`(HttpEncodings.gzip)
}
