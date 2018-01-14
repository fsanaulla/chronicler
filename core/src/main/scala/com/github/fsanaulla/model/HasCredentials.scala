package com.github.fsanaulla.model

trait HasCredentials {

  protected implicit val credentials: InfluxCredentials
}
