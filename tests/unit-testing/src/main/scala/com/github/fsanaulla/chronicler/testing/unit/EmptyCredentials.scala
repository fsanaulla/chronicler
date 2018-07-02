package com.github.fsanaulla.chronicler.testing.unit

import com.github.fsanaulla.chronicler.core.model.{HasCredentials, InfluxCredentials}

trait EmptyCredentials extends HasCredentials {
  val credentials: Option[InfluxCredentials] = None
}