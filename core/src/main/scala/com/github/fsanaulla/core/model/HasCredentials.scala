package com.github.fsanaulla.core.model

private[fsanaulla] trait HasCredentials {

  protected val credentials: Option[InfluxCredentials]
}
