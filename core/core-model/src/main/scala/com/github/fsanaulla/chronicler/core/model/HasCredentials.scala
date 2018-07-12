package com.github.fsanaulla.chronicler.core.model

/** Define functionality for using credentials in the context */
private[chronicler] trait HasCredentials {
  private[chronicler] val credentials: Option[InfluxCredentials]
}
