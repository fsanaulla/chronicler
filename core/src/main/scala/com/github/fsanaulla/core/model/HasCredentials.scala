package com.github.fsanaulla.core.model

trait HasCredentials {

  protected implicit val credentials: InfluxCredentials
}
