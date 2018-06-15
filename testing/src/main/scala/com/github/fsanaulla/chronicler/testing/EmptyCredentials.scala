package com.github.fsanaulla.chronicler.testing

import com.github.fsanaulla.chronicler.core.model.{HasCredentials, InfluxCredentials}

private[fsanaulla] trait EmptyCredentials extends HasCredentials {
  protected val credentials: Option[InfluxCredentials] = None
}