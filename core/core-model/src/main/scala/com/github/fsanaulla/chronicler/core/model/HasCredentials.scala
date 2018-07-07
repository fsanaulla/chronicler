package com.github.fsanaulla.chronicler.core.model

/**
  * Define functionality for using credentials in the context
  */
trait HasCredentials {
  protected val credentials: Option[InfluxCredentials]
}
