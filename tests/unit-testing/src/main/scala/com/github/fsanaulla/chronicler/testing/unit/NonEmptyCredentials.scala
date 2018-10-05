package com.github.fsanaulla.chronicler.testing.unit

import com.github.fsanaulla.chronicler.core.model.{HasCredentials, InfluxCredentials}

trait NonEmptyCredentials extends HasCredentials {
  implicit val credentials: Option[InfluxCredentials] =
    Some(InfluxCredentials("admin", "admin"))
}
