package com.github.fsanaulla

import com.github.fsanaulla.core.model.InfluxCredentials

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 16.08.17
  */
trait TestCredentials {

  implicit val credentials: InfluxCredentials = InfluxCredentials(Some("admin"), Some("admin"))

  val emptyCredentials = InfluxCredentials(None, None)

}
