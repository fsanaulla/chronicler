package com.github.fsanaulla.chronicler.testing

import com.github.fsanaulla.core.model.{HasCredentials, InfluxCredentials}

private[fsanaulla] trait NonEmptyCredentials extends HasCredentials {
  val credentials: Option[InfluxCredentials] =
    Some(InfluxCredentials("admin", "admin"))
}
