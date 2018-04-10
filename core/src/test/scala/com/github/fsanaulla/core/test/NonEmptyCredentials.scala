package com.github.fsanaulla.core.test

import com.github.fsanaulla.core.model.{HasCredentials, InfluxCredentials}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 16.08.17
  */
private[fsanaulla] trait NonEmptyCredentials extends HasCredentials {
  val credentials: Option[InfluxCredentials] =
    Some(InfluxCredentials("admin", "admin"))
}
