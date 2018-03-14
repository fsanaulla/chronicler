package com.github.fsanaulla.core.test.utils

import com.github.fsanaulla.core.model.{HasCredentials, InfluxCredentials}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 02.03.18
  */
trait EmptyCredentials extends HasCredentials {
  protected val credentials: Option[InfluxCredentials] = None
}
