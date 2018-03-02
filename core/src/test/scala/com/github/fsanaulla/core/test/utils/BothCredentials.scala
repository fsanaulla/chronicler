package com.github.fsanaulla.core.test.utils

import com.github.fsanaulla.core.model.InfluxCredentials

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 02.03.18
  */
/** Trait that provides credentials for testing query generation */
trait BothCredentials {
  val emptyCredentials: InfluxCredentials = InfluxCredentials(None, None)
  implicit val credentials: InfluxCredentials = InfluxCredentials(Some("admin"), Some("admin"))
}
