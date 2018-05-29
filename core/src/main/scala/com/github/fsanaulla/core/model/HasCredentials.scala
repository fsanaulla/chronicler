package com.github.fsanaulla.core.model

/**
  * Define functionality for using credentials in the context
  */
private[fsanaulla] trait HasCredentials {
  protected val credentials: Option[InfluxCredentials]
}
