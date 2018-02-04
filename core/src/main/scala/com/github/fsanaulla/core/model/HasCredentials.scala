package com.github.fsanaulla.core.model

private[fsanaulla] trait HasCredentials {

  protected implicit val credentials: InfluxCredentials
}
